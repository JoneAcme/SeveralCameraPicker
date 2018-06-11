package com.jone.several.model

import java.io.Serializable

/**
 * @fileName RxEvent
 * Created by YiangJone on 2018/6/6.
 * @describe
 */

data class EventEntity(var what:Int,var mediaEntities:MutableList<MediaEntity> ,var position:Int):Serializable