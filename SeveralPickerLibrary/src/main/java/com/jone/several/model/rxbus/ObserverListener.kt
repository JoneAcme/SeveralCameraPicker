package com.jone.several.model.rxbus

import com.jone.several.model.MediaEntity
import com.jone.several.model.MediaFolder

interface ObserverListener {
    fun observerUpFoldersData(folders: List<MediaFolder>)

    fun observerUpSelectsData(selectMediaEntities: List<MediaEntity>)
}
