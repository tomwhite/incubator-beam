/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.dataflow.sdk.runners.inprocess;

import com.google.cloud.dataflow.sdk.runners.inprocess.InMemoryWatermarkManager.TimerUpdate;
import com.google.cloud.dataflow.sdk.runners.inprocess.InMemoryWatermarkManager.TimerUpdate.TimerUpdateBuilder;
import com.google.cloud.dataflow.sdk.runners.inprocess.InMemoryWatermarkManager.TransformWatermarks;
import com.google.cloud.dataflow.sdk.util.TimerInternals;

import org.joda.time.Instant;

import javax.annotation.Nullable;

/**
 * An implementation of {@link TimerInternals} where all relevant data exists in memory.
 */
public class InProcessTimerInternals implements TimerInternals {
  private final Clock processingTimeClock;
  private final TransformWatermarks watermarks;
  private final TimerUpdateBuilder timerUpdateBuilder;

  public static InProcessTimerInternals create(
      Clock clock, TransformWatermarks watermarks, TimerUpdateBuilder timerUpdateBuilder) {
    return new InProcessTimerInternals(clock, watermarks, timerUpdateBuilder);
  }

  private InProcessTimerInternals(
      Clock clock, TransformWatermarks watermarks, TimerUpdateBuilder timerUpdateBuilder) {
    this.processingTimeClock = clock;
    this.watermarks = watermarks;
    this.timerUpdateBuilder = timerUpdateBuilder;
  }

  @Override
  public void setTimer(TimerData timerKey) {
    timerUpdateBuilder.setTimer(timerKey);
  }

  @Override
  public void deleteTimer(TimerData timerKey) {
    timerUpdateBuilder.deletedTimer(timerKey);
  }

  public TimerUpdate getTimerUpdate() {
    return timerUpdateBuilder.build();
  }

  @Override
  public Instant currentProcessingTime() {
    return processingTimeClock.now();
  }

  @Override
  @Nullable
  public Instant currentSynchronizedProcessingTime() {
    return watermarks.getSynchronizedProcessingInputTime();
  }

  @Override
  @Nullable
  public Instant currentInputWatermarkTime() {
    return watermarks.getInputWatermark();
  }

  @Override
  @Nullable
  public Instant currentOutputWatermarkTime() {
    return watermarks.getOutputWatermark();
  }
}

