package com.marcodomingues.noiseguard.ui.utils

import com.marcodomingues.noiseguard.domain.model.NoiseLevel

fun NoiseLevel.formattedDb(): String = "${decibels.toInt()} dB"
