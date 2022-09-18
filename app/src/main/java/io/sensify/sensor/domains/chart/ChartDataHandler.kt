package io.sensify.sensor.domains.chart

import android.hardware.SensorManager
import io.sensify.sensor.domains.chart.entity.ModelLineChart
import io.sensify.sensor.domains.sensors.SensorsConstants
import io.sensify.sensor.domains.sensors.packets.SensorPacket
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds

/**
 * Created by Niraj on 13-09-2022.
 */
class ChartDataHandler {

    private var mVisibleNum = -1
    private var mLengthSample = 100

    private val mLockDataAdd = Any()

    // TODO use this in ui handler
    private var mAddDirection = ChartConstants.DIRECTION_START_END
    var mModelLineChart: ModelLineChart

    var mPre = mutableListOf<SensorPacket>()

    var mUIRefreshDelay = SensorManager.SENSOR_DELAY_UI

    var mDataTypesIndexed = mutableListOf<Int>()

//    var mPre

    var mDataComputationScope = CoroutineScope(Job() + Dispatchers.Default)

    init {
        mModelLineChart = ModelLineChart(
            mLengthSample, mVisibleNum
        )
    }

    fun destroy() {

        mDataComputationScope.cancel()

    }

    fun addDataSet(
        dataType: Int,
        color: Int,
        label: String,
        data: Array<Float>,
        isHidden: Boolean
    ) {
        mDataTypesIndexed.add(dataType)

        // TODO check for data added
        mModelLineChart.addDataType(dataType, color, label, data, isHidden)

    }

    fun addEntry(sensorPacket: SensorPacket) {

        synchronized(mLockDataAdd) {
            mPre.add(sensorPacket)

        }
//        mModelLineChart.


    }

    fun runPeriodicTask() {

        mDataComputationScope.launch {
            while (mDataComputationScope.isActive) {
                // TODO should I periodic shift

                addPreEntry()
                delay(SensorsConstants.MAP_DELAY_TYPE_TO_DELAY.get(mUIRefreshDelay).seconds)


            }

        }


    }

    private fun addPreEntry(): Int {
        var preData: MutableList<SensorPacket>
        synchronized(mLockDataAdd) {
            preData = mPre
            mPre = mutableListOf()
        }

        val needToChangeUi = preData.size > 0

        for (item in preData) {

            for (index in mDataTypesIndexed) {

                if (item.values != null) {
                    if (item.values!!.size > index) {
                        mModelLineChart.addEntry(mDataTypesIndexed[index], item.values!![index]);
                    }
                }
                //loops all indices (performs just as well as two examples above)
            }
        }

        return preData.size;



//        for
//        preData

    }

    /*private fun shiftData(set: ILineDataSet) {
        if (set.entryCount > mModelLineChart!!.getSampleLength()) {
            set.removeEntry(0) // remove oldest
            // change Indexes - move to beginning by 1
            for (i in 1 until set.entryCount) {
                val entry = set.getEntryForIndex(i)
                entry.x = entry.x - 1
            }
        }
    }*/


}



