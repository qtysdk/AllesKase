package gaas.usecases

import gaas.repository.Database

interface QueryAvailableGameIds {
    abstract fun query(): List<String>

}

class QueryAvailableGameIdsImpl(private val database: Database) : QueryAvailableGameIds {
    override fun query(): List<String> {
        return database.listAvailableGameIds()
    }

}
