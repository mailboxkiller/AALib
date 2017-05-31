/*
 * LIMITED USE SOFTWARE LICENSE AGREEMENT
 *
 * This Limited Use Software License Agreement (the "Agreement") is a legal agreement between you, the end-user, and the AlgorithmicsAnonymous Team ("AlgorithmicsAnonymous"). By downloading or purchasing the software material, which includes source code (the "Source Code"), artwork data, music and software tools (collectively, the "Software"), you are agreeing to be bound by the terms of this Agreement. If you do not agree to the terms of this Agreement, promptly destroy the Software you may have downloaded or copied.
 *
 * AlgorithmicsAnonymous SOFTWARE LICENSE
 *
 * 1. Grant of License. AlgorithmicsAnonymous grants to you the right to use the Software. You have no ownership or proprietary rights in or to the Software, or the Trademark. For purposes of this section, "use" means loading the Software into RAM, as well as installation on a hard disk or other storage device. The Software, together with any archive copy thereof, shall be destroyed when no longer used in accordance with this Agreement, or when the right to use the Software is terminated. You agree that the Software will not be shipped, transferred or exported into any country in violation of the U.S. Export Administration Act (or any other law governing such matters) and that you will not utilize, in any other manner, the Software in violation of any applicable law.
 *
 * 2. Permitted Uses. For educational purposes only, you, the end-user, may use portions of the Source Code, such as particular routines, to develop your own software, but may not duplicate the Source Code, except as noted in paragraph 4. The limited right referenced in the preceding sentence is hereinafter referred to as "Educational Use." By so exercising the Educational Use right you shall not obtain any ownership, copyright, proprietary or other interest in or to the Source Code, or any portion of the Source Code. You may dispose of your own software in your sole discretion. With the exception of the Educational Use right, you may not otherwise use the Software, or an portion of the Software, which includes the Source Code, for commercial gain.
 *
 * 3. Prohibited Uses: Under no circumstances shall you, the end-user, be permitted, allowed or authorized to commercially exploit the Software. Neither you nor anyone at your direction shall do any of the following acts with regard to the Software, or any portion thereof:
 *
 * Rent;
 *
 * Sell;
 *
 * Lease;
 *
 * Offer on a pay-per-play basis;
 *
 * Distribute for money or any other consideration; or
 *
 * In any other manner and through any medium whatsoever commercially exploit or use for any commercial purpose.
 *
 * Notwithstanding the foregoing prohibitions, you may commercially exploit the software you develop by exercising the Educational Use right, referenced in paragraph 2. hereinabove.
 *
 * 4. Copyright. The Software and all copyrights related thereto (including all characters and other images generated by the Software or depicted in the Software) are owned by AlgorithmicsAnonymous and is protected by United States copyright laws and international treaty provisions. AlgorithmicsAnonymous shall retain exclusive ownership and copyright in and to the Software and all portions of the Software and you shall have no ownership or other proprietary interest in such materials. You must treat the Software like any other copyrighted material. You may not otherwise reproduce, copy or disclose to others, in whole or in any part, the Software. You may not copy the written materials accompanying the Software. You agree to use your best efforts to see that any user of the Software licensed hereunder complies with this Agreement.
 *
 * 5. NO WARRANTIES. AlgorithmicsAnonymous DISCLAIMS ALL WARRANTIES, BOTH EXPRESS IMPLIED, INCLUDING BUT NOT LIMITED TO, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE WITH RESPECT TO THE SOFTWARE. THIS LIMITED WARRANTY GIVES YOU SPECIFIC LEGAL RIGHTS. YOU MAY HAVE OTHER RIGHTS WHICH VARY FROM JURISDICTION TO JURISDICTION. AlgorithmicsAnonymous DOES NOT WARRANT THAT THE OPERATION OF THE SOFTWARE WILL BE UNINTERRUPTED, ERROR FREE OR MEET YOUR SPECIFIC REQUIREMENTS. THE WARRANTY SET FORTH ABOVE IS IN LIEU OF ALL OTHER EXPRESS WARRANTIES WHETHER ORAL OR WRITTEN. THE AGENTS, EMPLOYEES, DISTRIBUTORS, AND DEALERS OF AlgorithmicsAnonymous ARE NOT AUTHORIZED TO MAKE MODIFICATIONS TO THIS WARRANTY, OR ADDITIONAL WARRANTIES ON BEHALF OF AlgorithmicsAnonymous.
 *
 * Exclusive Remedies. The Software is being offered to you free of any charge. You agree that you have no remedy against AlgorithmicsAnonymous, its affiliates, contractors, suppliers, and agents for loss or damage caused by any defect or failure in the Software regardless of the form of action, whether in contract, tort, includinegligence, strict liability or otherwise, with regard to the Software. Copyright and other proprietary matters will be governed by United States laws and international treaties. IN ANY CASE, AlgorithmicsAnonymous SHALL NOT BE LIABLE FOR LOSS OF DATA, LOSS OF PROFITS, LOST SAVINGS, SPECIAL, INCIDENTAL, CONSEQUENTIAL, INDIRECT OR OTHER SIMILAR DAMAGES ARISING FROM BREACH OF WARRANTY, BREACH OF CONTRACT, NEGLIGENCE, OR OTHER LEGAL THEORY EVEN IF AlgorithmicsAnonymous OR ITS AGENT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR FOR ANY CLAIM BY ANY OTHER PARTY. Some jurisdictions do not allow the exclusion or limitation of incidental or consequential damages, so the above limitation or exclusion may not apply to you.
 */

package xyz.aadev.aalib.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import xyz.aadev.aalib.common.container.slot.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class ContainerBase extends Container {
    final InventoryPlayer inventoryPlayer;
    final TileEntity tileEntity;
    public boolean isContainerValid = true;
    protected HashSet<Integer> locked = new HashSet<Integer>();

    public ContainerBase(InventoryPlayer inventoryPlayer, TileEntity tileEntity) {
        this.inventoryPlayer = inventoryPlayer;
        this.tileEntity = tileEntity;
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer, int offsetX, int offsetY) {
        // Get player inventory slots...
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.locked.contains(Integer.valueOf(j + i * 9 + 9))) {
                    addSlotToContainer(new SlotDisabled(inventoryPlayer, j + i * 9 + 9, 8 + j * 18 + offsetX, offsetY + i * 18));
                } else {
                    addSlotToContainer(new SlotPlayerInventory(inventoryPlayer, j + i * 9 + 9, 8 + j * 18 + offsetX, offsetY + i * 18));
                }
            }
        }

        // Get hotbar slots...
        for (int i = 0; i < 9; i++) {
            if (this.locked.contains(Integer.valueOf(i))) {
                addSlotToContainer(new SlotDisabled(inventoryPlayer, i, 8 + i * 18 + offsetX, 58 + offsetY));
            } else {
                addSlotToContainer(new SlotPlayerHotBar(inventoryPlayer, i, 8 + i * 18 + offsetX, 58 + offsetY));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (this.isContainerValid) {
            if ((this.tileEntity instanceof IInventory))
                return ((IInventory) this.tileEntity).isUsableByPlayer(player);

            return true;
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int idx) {
        //todo: refactor this mess
        ItemStack itemStack ;

        SlotBase clickSlot = (SlotBase) this.inventorySlots.get(idx);
        if (clickSlot instanceof SlotDisabled) {
            return ItemStack.EMPTY;
        }

        if ((clickSlot != null) && (clickSlot.getHasStack())) {
            itemStack = clickSlot.getStack();
            if (itemStack == ItemStack.EMPTY) {
                return ItemStack.EMPTY;
            }

            NonNullList<Slot> selectedSlots = NonNullList.create();
            if (clickSlot.isPlayerSide()) {
                //itemStack = shiftStoreItem(itemStack);
                for (int x = 0; x < this.inventorySlots.size(); x++) {
                    SlotBase advSlot = (SlotBase) this.inventorySlots.get(x);
                    if ((!advSlot.isPlayerSide()) && (!(advSlot instanceof SlotFake))) {
                        if (advSlot.isItemValid(itemStack)) {
                            selectedSlots.add(advSlot);
                        }
                    }
                }
            } else {
                for (int x = 0; x < this.inventorySlots.size(); x++) {
                    SlotBase advSlot = (SlotBase) this.inventorySlots.get(x);
                    if ((advSlot.isPlayerSide()) && (!(advSlot instanceof SlotFake))) {
                        if (advSlot.isItemValid(itemStack)) {
                            selectedSlots.add(advSlot);
                        }
                    }
                }
            }

            if ((selectedSlots.isEmpty()) && (clickSlot.isPlayerSide())) {
                if (itemStack != ItemStack.EMPTY) {
                    for (int x = 0; x < this.inventorySlots.size(); x++) {
                        SlotBase advSlot = (SlotBase) this.inventorySlots.get(x);
                        ItemStack dest = advSlot.getStack();
                        if ((!advSlot.isPlayerSide()) && (advSlot instanceof SlotFake)) {
                            if (dest == ItemStack.EMPTY) {
                                advSlot.putStack(itemStack != ItemStack.EMPTY ? itemStack.copy() : ItemStack.EMPTY);
                                advSlot.onSlotChanged();
                                updateSlot(advSlot);
                                return null;
                            }
                        }
                    }
                }
            }

            if (itemStack != ItemStack.EMPTY) {
                for (Slot d : selectedSlots) {
                    if ((!(d instanceof SlotDisabled))) {
                        if ((d.isItemValid(itemStack)) && (itemStack != ItemStack.EMPTY)) {
                            if (d.getHasStack()) {
                                ItemStack t = d.getStack();
                                if ((itemStack != ItemStack.EMPTY) && (itemStack.isItemEqual(t))) {
                                    int maxSize = t.getMaxStackSize();
                                    if (maxSize > d.getSlotStackLimit()) {
                                        maxSize = d.getSlotStackLimit();
                                    }
                                    int placeAble = maxSize - t.getCount();
                                    if (itemStack.getCount() < placeAble) {
                                        placeAble = itemStack.getCount();
                                    }
                                    t.grow(placeAble);
                                    itemStack.shrink(placeAble);
                                    if (itemStack.getCount() <= 0) {
                                        clickSlot.putStack(ItemStack.EMPTY);
                                        d.onSlotChanged();

                                        updateSlot(clickSlot);
                                        updateSlot(d);
                                        return ItemStack.EMPTY;
                                    }
                                    updateSlot(d);
                                }
                            }
                        }
                    }
                }

                for (Slot d : selectedSlots) {
                    if ((!(d instanceof SlotDisabled))) {
                        if ((d.isItemValid(itemStack)) && (itemStack != ItemStack.EMPTY)) {
                            if (d.getHasStack()) {
                                ItemStack t = d.getStack();
                                if ((itemStack != ItemStack.EMPTY) && (itemStack.isItemEqual(t))) {
                                    int maxSize = t.getMaxStackSize();
                                    if (maxSize > d.getSlotStackLimit()) {
                                        maxSize = d.getSlotStackLimit();
                                    }
                                    int placeAble = maxSize - t.getCount();
                                    if (itemStack.getCount() < placeAble) {
                                        placeAble = itemStack.getCount();
                                    }
                                    t.grow(placeAble);
                                    itemStack.shrink(placeAble);
                                    if (itemStack.getCount() <= 0) {
                                        clickSlot.putStack(ItemStack.EMPTY);
                                        d.onSlotChanged();

                                        updateSlot(clickSlot);
                                        updateSlot(d);
                                        return ItemStack.EMPTY;
                                    }
                                    updateSlot(d);
                                }
                            } else {
                                int maxSize = itemStack.getMaxStackSize();
                                if (maxSize > d.getSlotStackLimit()) {
                                    maxSize = d.getSlotStackLimit();
                                }
                                ItemStack tmp = itemStack.copy();
                                if (tmp.getCount() > maxSize) {
                                    tmp.setCount(maxSize);
                                }
                                itemStack.shrink(tmp.getCount());
                                d.putStack(tmp);
                                if (itemStack.getCount() <= 0) {
                                    clickSlot.putStack(ItemStack.EMPTY);
                                    d.onSlotChanged();

                                    updateSlot(clickSlot);
                                    updateSlot(d);
                                    return ItemStack.EMPTY;
                                }
                                updateSlot(d);
                            }
                        }
                    }
                }
            }

            clickSlot.putStack(itemStack != ItemStack.EMPTY ? itemStack.copy() : ItemStack.EMPTY);
        }
        updateSlot(clickSlot);
        return ItemStack.EMPTY;
    }

    private void updateSlot(final Slot slot) {
        this.detectAndSendChanges();
    }
}
