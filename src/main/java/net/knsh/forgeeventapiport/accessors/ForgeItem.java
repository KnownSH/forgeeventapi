package net.knsh.forgeeventapiport.accessors;

import net.knsh.forgeeventapiport.neoforge.common.ToolAction;
import net.minecraft.world.item.ItemStack;

public interface ForgeItem {
    /**
     * Queries if an item can perform the given action.
     * See {@link net.knsh.forgeeventapiport.neoforge.common.ToolActions} for a description of each stock action
     *
     * @param stack      The stack being used
     * @param toolAction The action being queried
     * @return True if the stack can perform the action
     */
    default boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return false;
    }
}
