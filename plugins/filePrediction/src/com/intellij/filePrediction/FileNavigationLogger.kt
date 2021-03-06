// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.filePrediction

import com.intellij.internal.statistic.eventLog.FeatureUsageData
import com.intellij.internal.statistic.service.fus.collectors.FUCounterUsageLogger
import com.intellij.openapi.project.Project

internal object FileNavigationLogger {
  private const val GROUP_ID = "file.prediction"

  fun logEvent(project: Project,
               event: String,
               sessionId: Int,
               features: FileFeaturesComputationResult,
               filePath: String,
               prevFilePath: String?,
               totalDuration: Long,
               refsComputation: Long,
               predictionDuration: Long? = null,
               probability: Double? = null) {
    val data = FeatureUsageData()
      .addData("session_id", sessionId)
      .addAnonymizedPath(filePath)
      .addAnonymizedValue("prev_file_path", prevFilePath)
      .addData("total_ms", totalDuration)
      .addData("refs_ms", refsComputation)
      .addData("features_ms", features.duration)

    if (predictionDuration != null) {
      data.addData("predict_ms", predictionDuration)
    }

    if (probability != null) {
      data.addData("probability", probability)
    }

    for (feature in features.value) {
      feature.value.addToEventData(feature.key, data)
    }
    FUCounterUsageLogger.getInstance().logEvent(project, GROUP_ID, event, data)
  }
}