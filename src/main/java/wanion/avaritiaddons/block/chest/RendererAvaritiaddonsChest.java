package wanion.avaritiaddons.block.chest;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class RendererAvaritiaddonsChest extends TileEntitySpecialRenderer
{
	public static final RendererAvaritiaddonsChest instance = new RendererAvaritiaddonsChest();

	private RendererAvaritiaddonsChest() {}

	public void renderTileEntityAt(final TileEntity tileEntity, final double x, final double y, final double z, final float distance)
	{
		final TileEntityAvaritiaddonsChest tileEntityAvaritiaddonsChest = (TileEntityAvaritiaddonsChest) tileEntity;
		bindTexture(tileEntityAvaritiaddonsChest.getTexture());
		final ModelChest modelchest = tileEntityAvaritiaddonsChest.modelChest;
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		GL11.glRotatef(tileEntityAvaritiaddonsChest.hasWorldObj() ? tileEntityAvaritiaddonsChest.getFacingSide() : -90, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		float f1 = tileEntityAvaritiaddonsChest.prevLidAngle + (tileEntityAvaritiaddonsChest.lidAngle - tileEntityAvaritiaddonsChest.prevLidAngle) * distance;

		f1 = 1.0F - f1;
		f1 = 1.0F - f1 * f1 * f1;
		modelchest.chestLid.rotateAngleX = -(f1 * (float)Math.PI / 2.0F);
		modelchest.renderAll();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}