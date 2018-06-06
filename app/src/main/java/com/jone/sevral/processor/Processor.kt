package com.jone.sevral.processor

import android.content.Context
import com.jone.sevral.config.PickerOption
import com.jone.sevral.model.MediaEntity
import com.jone.sevral.processor.listener.OnProcessorListener

/**
 * @fileName Processor
 * Created by YiangJone on 2018/6/6.
 * @describe
 */


interface Processor {


    /**
     * 同步处理任务
     *
     * @param context      context
     * @param mediaEntity  mediaEntity
     * @param phoenixOption phoenixOption
     */
    fun syncProcess(context: Context, mediaEntity: MediaEntity, phoenixOption: PickerOption): MediaEntity

    /**
     * 异步处理任务
     *
     * @param context             context
     * @param mediaEntity         mediaEntity
     * @param pickerOption        option
     * @param onProcessorListener listener
     */
    fun asyncProcess(context: Context, mediaEntity: MediaEntity, pickerOption:  PickerOption, onProcessorListener: OnProcessorListener)
}
