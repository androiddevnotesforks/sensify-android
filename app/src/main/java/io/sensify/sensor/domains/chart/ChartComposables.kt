package io.sensify.sensor.domains.chart

import android.content.Context
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import io.sensify.sensor.domains.chart.entity.ModelChartUiUpdate
import io.sensify.sensor.domains.chart.mpchart.MpChartViewManager
import io.sensify.sensor.domains.sensors.packets.SensorPacketConfig
import io.sensify.sensor.domains.sensors.packets.SensorPacketsProvider
import io.sensify.sensor.domains.sensors.sensorManagerProvider
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * Created by Niraj on 19-09-2022.
 */
@Composable
fun rememberChartUiUpdateEvent(
    mpChartViewManager: MpChartViewManager,
    sensorDelay: Int
): State<ModelChartUiUpdate> {
    val coroutineScope = rememberCoroutineScope()

//    var sensorManager = sensorManagerProvider()

    /*
    val flow =
        sensorFlow.filter { sensorPacket ->
            var filtered = sensorPacket.type == mpChartViewManager.sensorType
            // sensorPacket.sensorEvent?.values
//            Log.d("rememberChartUiUpdateEvent", "filtered: $filtered, ${mpChartViewManager.sensorType}")
            return@filter filtered
        }*/

    var sensorFlow =
        SensorPacketsProvider.getInstance().mSensorPacketFlow
    val flow =
        sensorFlow.filter { sensorPacket ->
            var filtered = sensorPacket.type == mpChartViewManager.sensorType
            // sensorPacket.sensorEvent?.values
//            Log.d("rememberChartUiUpdateEvent", "filtered: $filtered, ${mpChartViewManager.sensorType}")
            return@filter filtered
        }
    val context = LocalContext.current

    // TODO fix chart view updated
    LaunchedEffect(key1 = context) {

        var sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        SensorPacketsProvider.getInstance().setSensorManager(sensorManager).attachSensor(
            SensorPacketConfig(mpChartViewManager.sensorType, sensorDelay)
        )
        coroutineScope.launch {
            flow.collect {
                mpChartViewManager.addEntry(it)
            }
        }
    }

    var state = mpChartViewManager.mSensorPacketFlow.collectAsState(
        initial = ModelChartUiUpdate(
            sensorType = mpChartViewManager.sensorType,
            0,
            listOf()
        )
    )
    val state1 = remember {
        state
    };

    return state1;

}

