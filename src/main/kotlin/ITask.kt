import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class ITask {
    constructor()
    constructor(vararg tasks: BukkitTask) {
        this.tasks.addAll(tasks.map { SingleTask(it) })
    }
    constructor(vararg tasks: Int) {
        this.tasks.addAll(tasks.map { SingleTask(taskId = it) })
    }
    val tasks = ArrayList<SingleTask>()
    fun cancel() {
        tasks.forEach {
            val bukkitTask = it.bukkitTask
            if (bukkitTask === null) Bukkit.getScheduler().cancelTask(it.taskId)
            else if (!bukkitTask.isCancelled) bukkitTask.cancel()
        }
    }
    data class SingleTask(val bukkitTask: BukkitTask? = null, val taskId: Int = if(bukkitTask === null) -1 else bukkitTask.taskId)
    companion object {
        @JvmStatic
        fun repeat(plugin: Plugin, delay: Long, period: Long, vararg functions: () -> Unit): ITask {
            return if (functions.size == 1)
                ITask(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, functions[0], delay, period))
            else ITask().apply {
                functions.mapIndexed { index, runnable ->
                    tasks.addAll(runnable.delay(plugin, delay + period * index).tasks)
                }
            }
        }
    }
}


fun <T> (() -> T).delay(plugin: Plugin, delay: Long) =
    ITask(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, { this() }, delay))
fun (() -> Boolean).repeat(plugin: Plugin, delay: Long, period: Long) =
    ITask(object : BukkitRunnable() {
        override fun run() {
            if (!this@repeat()) {
                cancel()
            }
        }
    }.runTaskTimer(plugin, delay, period))
fun <T> (() -> T).runNow(plugin: Plugin) = ITask(Bukkit.getScheduler().runTask(plugin, Runnable { this() }))
fun <T> (() -> T).async(plugin: Plugin) = ITask(Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable { this() }))
