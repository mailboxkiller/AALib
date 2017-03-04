package xyz.aadev.aalib.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import xyz.aadev.aalib.common.util.WorldInfoHelper;

import java.text.DecimalFormat;

public class CommandTPS extends CommandBase {
    private static DecimalFormat floatfmt = new DecimalFormat("##0.00");

    @Override
    public String getName() {
        return "tps";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "tps";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        double tps = WorldInfoHelper.getTps();

        sender.sendMessage(new TextComponentString("Overall: " + floatfmt.format(tps) + " TPS (" + (int) (tps / 20.0D * 100.0D) + "%)"));

        for (WorldServer worldServer : server.worlds) {
            tps = WorldInfoHelper.getTps(worldServer);

            sender.sendMessage(new TextComponentString(worldServer.provider.getDimensionType() + " [" + worldServer.provider.getDimension() + "]: "
                    + floatfmt.format(tps) + " TPS (" + (int) (tps / 20.0D * 100.0D) + "%)"));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
