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
package org.enderstone.server.regions.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.enderstone.server.regions.BlockId;
import org.enderstone.server.regions.BlockPopulator;
import org.enderstone.server.regions.ChunkGenerator;
import org.enderstone.server.regions.EnderChunk;
import org.enderstone.server.regions.EnderWorld;

public class FlatLandGenerator implements ChunkGenerator {
	@Override
	public BlockId[][] generateExtBlockSections(EnderWorld world, Random random, int x, int z) {
		BlockId[][] r = new BlockId[16][];

		r[0] = new BlockId[4096];
		for (int i = 0; i < r[0].length; i++) {
			r[0][i] = BlockId.DIRT;
		}
		
		r[1] = new BlockId[4096];
		for (int i = 0; i < r[0].length; i++) {
			r[1][i] = BlockId.DIRT;
		}
		
		r[2] = new BlockId[4096];
		for (int i = 0; i < r[0].length; i++) {
			r[2][i] = BlockId.DIRT;
		}
		
		r[3] = new BlockId[4096];
		for (int i = 0; i < r[0].length; i++) {
			r[1][3] = BlockId.DIRT;
		}
		
		return r;
	}

	@Override
	public List<MultiChunkBlockPopulator> getDefaultPopulators(EnderWorld world) {
		List<MultiChunkBlockPopulator> p = new ArrayList<>();
		p.add(new BlockPopulator() {

			@Override
			public void populate(EnderWorld world, Random random, EnderChunk source) {
				source.setBlock(7, 16, 7, BlockId.COBBLESTONE, (byte) 0);
			}
		});
		return p;
	}
}
