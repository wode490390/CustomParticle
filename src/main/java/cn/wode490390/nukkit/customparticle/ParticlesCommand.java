package cn.wode490390.nukkit.customparticle;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginIdentifiableCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
import cn.nukkit.plugin.Plugin;
import java.util.Arrays;

public class ParticlesCommand extends Command implements PluginIdentifiableCommand {

    private final Plugin plugin;

    public ParticlesCommand(Plugin plugin) {
        super("particles", "Creates a particle emitter", "/particles <effect> [position]");
        this.setPermission("particles.command");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                new CommandParameter("effect", CommandParamType.STRING, false),
                new CommandParameter("position", CommandParamType.POSITION, true)
        });
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.plugin.isEnabled() || !this.testPermission(sender)) {
            return false;
        }
        if (sender instanceof Position) {
            Position pos = (Position) sender;
            double x;
            double y;
            double z;
            if (args.length > 5) {
                sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/particles " + String.join(" ", Arrays.copyOfRange(args, 0, 3)) + " ", args[4], " " + String.join(" ", Arrays.copyOfRange(args, 5, args.length))));
                return false;
            } else if (args.length == 5) {
                sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/particles " + String.join(" ", Arrays.copyOfRange(args, 0, 3)) + " ", args[4]));
                return false;
            } else if (args.length == 0) {
                sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/particles"));
                return false;
            } else if (args.length == 4) {
                try {
                    x = parsePosition(args[1], pos.getX());
                } catch (Exception e) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/particles " + args[0] + " ", args[1]));
                    return false;
                }
                try {
                    y = parsePosition(args[2], pos.getY());
                } catch (Exception e) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/particles " + String.join(" ", Arrays.copyOfRange(args, 0, 1)) + " ", args[2]));
                    return false;
                }
                try {
                    z = parsePosition(args[3], pos.getZ());
                } catch (Exception e) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/particles " + String.join(" ", Arrays.copyOfRange(args, 0, 2)) + " ", args[3]));
                    return false;
                }
            } else if (args.length != 1) {
                sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/particles " + String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1))));
                return false;
            } else {
                x = pos.getX();
                y = pos.getY();
                z = pos.getZ();
            }

            SpawnParticleEffectPacket pk = new SpawnParticleEffectPacket(); // 此数据包用于调用客户端的颗粒效果
            pk.position = new Vector3f((float) x, (float) y, (float) z); // 生成颗粒效果的位置
            pk.identifier = args[0]; // 颗粒效果定义符, 必须和材质包内设定的一样, 否则不会显示
            pk.dimensionId = 0; // 维度ID, 填玩家所在世界维度的即可, 默认为 0 (0: 主世界, 1: 地狱, 2: 末地)
            pk.uniqueEntityId = -1; // 某实体的UUID, 目前无需理会, 默认为 -1

            pos.getLevel().addChunkPacket((int) x >> 4, (int) z >> 4, pk);
        } else {
            sender.sendMessage(new TranslationContainer("%commands.generic.ingame"));
            return false;
        }
        return true;
    }

    private static double parsePosition(String arg, double defaultValue) throws Exception {
        if (arg.startsWith("~")) {
            String relativePos = arg.substring(1);
            if (relativePos.isEmpty()) {
                return defaultValue;
            }
            return defaultValue + Double.parseDouble(relativePos);
        }
        return Double.parseDouble(arg);
    }
}
