package org.readutf.game.engine.schedular

import io.github.oshai.kotlinlogging.KotlinLogging
import net.minestom.server.MinecraftServer
import net.minestom.server.timer.TaskSchedule
import org.readutf.game.engine.Game
import org.readutf.game.engine.stage.Stage

class GameScheduler(
    game: Game<*>,
) {
    private val logger = KotlinLogging.logger { }

    private val globalTasks = mutableSetOf<GameTask>()
    private val stageTasks = mutableMapOf<Stage, MutableSet<GameTask>>() // Stage -> Tasks

    init {
        logger.info { "Starting scheduler" }

        MinecraftServer.getSchedulerManager().scheduleTask({
            globalTasks.forEach(::tickTask)

            if (game.currentStage == null) return@scheduleTask

            stageTasks[game.currentStage]?.forEach(::tickTask)
        }, TaskSchedule.tick(1), TaskSchedule.tick(1))
    }

    private fun tickTask(task: GameTask) {
        if (task is RepeatingGameTask) {
            if (System.currentTimeMillis() - task.startTime >= task.delay) {
                task.tick()
            }
        } else if (task is DelayedGameTask) {
            if (System.currentTimeMillis() - task.startTime >= task.delay) {
                task.tick()
                globalTasks.remove(task)
            }
        }
    }

    fun schedule(
        stage: Stage,
        task: () -> Unit,
    ) {
        schedule(
            stage,
            object : DelayedGameTask(0) {
                override fun tick() {
                    task()
                }
            },
        )
    }

    fun schedule(runnable: () -> Unit) {
        schedule(
            object : DelayedGameTask(0) {
                override fun tick() {
                    runnable()
                }
            },
        )
    }

    fun schedule(
        stage: Stage,
        runnable: () -> Unit,
        delay: Long,
    ) {
        schedule(
            stage,
            object : DelayedGameTask(delay) {
                override fun tick() {
                    runnable()
                }
            },
        )
    }

    fun schedule(
        runnable: () -> Unit,
        delay: Long,
    ) {
        schedule(
            object : DelayedGameTask(delay) {
                override fun tick() {
                    runnable()
                }
            },
        )
    }

    fun schedule(
        stage: Stage,
        runnable: () -> Unit,
        delay: Long,
        period: Long,
    ) {
        schedule(
            stage,
            object : RepeatingGameTask(delay, period) {
                override fun tick() {
                    runnable()
                }
            },
        )
    }

    fun schedule(
        runnable: () -> Unit,
        delay: Long,
        period: Long,
    ) {
        schedule(
            object : RepeatingGameTask(delay, period) {
                override fun tick() {
                    runnable()
                }
            },
        )
    }

    fun schedule(gameTask: GameTask) {
        logger.info { "Scheduling task $gameTask" }
        globalTasks.add(gameTask)
    }

    fun schedule(
        stage: Stage,
        gameTask: GameTask,
    ) {
        logger.info { "Scheduling task `${gameTask::class.simpleName}` for stage `${stage::class.simpleName}`" }
        stageTasks.computeIfAbsent(stage) { mutableSetOf() }.add(gameTask)
    }
}
