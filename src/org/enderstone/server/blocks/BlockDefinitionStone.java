/* 
 * Enderstone
 * Copyright (C) 2014 Sander Gielisse and Fernando van Loenhout
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.enderstone.server.blocks;

import org.enderstone.server.api.World;
import org.enderstone.server.api.entity.Player;
import org.enderstone.server.inventory.ItemStack;
import org.enderstone.server.regions.BlockId;

/**
 *
 * @author gyroninja
 */
public class BlockDefinitionStone extends BlockDefinition {

	public BlockDefinitionStone() {
		super(BlockId.STONE);
	}

	@Override
	public String getPlaceSound() {
		return "step.stone";
	}

	@Override
	public String getBreakSound() {
		return "dig.stone";
	}

	@Override
	public ItemStack getDrop(Player player, World world, int x, int y, int z) {
		if (world.getBlock(x, y, z).getData() == 0) {
			return new ItemStack(BlockId.COBBLESTONE);
		} else {
			return super.getDrop(player, world, x, y, z);
		}
	}
}
