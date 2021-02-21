package org.dbpedia.databus_mods.server.core.execution

import org.dbpedia.databus_mods.server.core.persistence.{Task, Worker}

class WorkerThreadException(task: Task, worker: Worker) extends Exception {


}
