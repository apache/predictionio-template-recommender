package org.template.recommendation

import io.prediction.controller.Evaluation
import io.prediction.controller.OptionAverageMetric
import io.prediction.controller.AverageMetric
import io.prediction.controller.EmptyEvaluationInfo
import io.prediction.controller.EngineParamsGenerator
import io.prediction.controller.EngineParams
import io.prediction.controller.MetricEvaluator

// Usage: 
// $ pio eval org.template.recommendation.RecommendationEvaluation \
//   org.template.recommendation.ParamsList

case class PrecisionAtK(k: Int, ratingThreshold: Double = 2.0)
    extends OptionAverageMetric[EmptyEvaluationInfo, Query, PredictedResult, ActualResult] {
  require(k > 0, "k must be greater than 0")

  override def header = s"Precision@K ($k, $ratingThreshold)"

  def calculate(q: Query, p: PredictedResult, a: ActualResult): Option[Double] = {
    val positives: Set[String] = a.ratings.filter(_.rating >= ratingThreshold).map(_.item).toSet

    if (positives.size == 0) {
      return None
    }
  
    val tpCount: Int = p.itemScores.take(k).filter(is => positives(is.item)).size

    Some(tpCount.toDouble / math.min(k, positives.size))
  }
}

case class PositiveCount(ratingThreshold: Double = 2.0)
    extends AverageMetric[EmptyEvaluationInfo, Query, PredictedResult, ActualResult] {
  override def header = s"PositiveCount ($ratingThreshold)"

  def calculate(q: Query, p: PredictedResult, a: ActualResult): Double = {
    a.ratings.filter(_.rating >= ratingThreshold).size
  }
}

object RecommendationEvaluation extends Evaluation {
  engineEvaluator = (
    RecommendationEngine(),
    MetricEvaluator(
      metric = PrecisionAtK(k = 10, ratingThreshold = 4.0),
      otherMetrics = Seq(
        PositiveCount(ratingThreshold = 4.0),
        PrecisionAtK(k = 10, ratingThreshold = 2.0),
        PositiveCount(ratingThreshold = 2.0),
        PrecisionAtK(k = 10, ratingThreshold = 1.0),
        PositiveCount(ratingThreshold = 1.0)
      )))
}

object ParamsList extends EngineParamsGenerator {
  private[this] val baseEP = EngineParams(
    dataSourceParams = DataSourceParams(
      appId = 21, 
      evalParams = Some(DataSourceEvalParams(kFold = 5, queryNum = 10))))

  engineParamsList = Seq(
    baseEP.copy(
      algorithmParamsList = Seq(("als", ALSAlgorithmParams(10, 20, 0.01, Some(3))))),
    baseEP.copy(
      algorithmParamsList = Seq(("als", ALSAlgorithmParams(10, 40, 0.01, Some(3))))),
    baseEP.copy(
      algorithmParamsList = Seq(("als", ALSAlgorithmParams(10, 80, 0.01, Some(3))))))
}
