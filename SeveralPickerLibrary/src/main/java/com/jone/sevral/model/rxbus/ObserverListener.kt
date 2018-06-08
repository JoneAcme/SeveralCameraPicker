package com.jone.sevral.model.rxbus

import com.jone.sevral.model.MediaEntity
import com.jone.sevral.model.MediaFolder

interface ObserverListener {
    fun observerUpFoldersData(folders: List<MediaFolder>)

    fun observerUpSelectsData(selectMediaEntities: List<MediaEntity>)
}
