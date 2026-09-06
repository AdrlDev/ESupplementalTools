package com.esupplemental.data.local.datasource

import com.esupplemental.data.model.game.GameCatalogue
import com.esupplemental.domain.model.game.GameItem

class StaticGameDataSource {
    companion object {
        val all: List<GameItem> get() = GameCatalogue.all
    }
}