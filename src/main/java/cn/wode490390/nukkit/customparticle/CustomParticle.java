package cn.wode490390.nukkit.customparticle;

import cn.nukkit.plugin.PluginBase;

public class CustomParticle extends PluginBase {

    @Override
    public void onEnable() {
        this.getServer().getCommandMap().register("customparticle", new ParticlesCommand(this));
        try {
            new MetricsLite(this);
        } catch (Throwable ignore) {

        }
        this.saveResource("NukkitCustomParticleExampleResourcesPack.mcpack");
    }
}
