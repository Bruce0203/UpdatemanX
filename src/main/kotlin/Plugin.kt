import io.github.inggameteam.command.MCCommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused")
class Plugin : JavaPlugin() {

    val semaphore = ConcurrentHashMap<String, Boolean>()

    override fun onEnable() {
        MCCommand(this) {
            command("updateman") {
                config.getKeys(false).forEach { key ->
                    if (config.isSet("$key.watchdog")) {
                        var func: (() -> Unit)? = null
                        func = block@{
                            if (semaphore[key] === null) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "updateman $key")
                            }
                            ;{ func!!.invoke() }.delay(this@Plugin, 20)
                        }
                        ;{ func.invoke() }.delay(this@Plugin, 20)
                    }
                    then(key) {
                        execute {
                            val section = config.getConfigurationSection(key)!!
                            if (section.getBoolean("pull")) {
                                pull(
                                    section.getString("destiny")!!
                                )
                            } else {
                                Update(
                                    key,
                                    this@Plugin,
                                    section.getString("plugin")!!,
                                    section.getString("url")!!,
                                    File(dataFolder, key),
                                    section.getString("cmd")!!,
                                    section.getString("out")!!,
                                    section.getString("branch")!!,
                                )
                            }
                        }
                    }

                }
            }
        }
    }


}