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
package org.enderstone.server.regions;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

/**
 * This Set is used to store the chunks in a HashSet manner from a world, it generally has better performance over a
 * HashSet for chunks since its focussed on storing chunks from a region together in the same subnode
 *
 * @author ferrybig
 */
public class RegionSet extends AbstractSet<EnderChunk> {

	private static final int REGION_BUCKET_SIZE = 32 * 32;
	
	private static final int CHUNK_BUCKET_SIZE = 16;
	private Node[][] chunkBuckets = new Node[CHUNK_BUCKET_SIZE][CHUNK_BUCKET_SIZE];

	private static class Node {

		public Node(int regionX, int regionZ) {
			this.regionX = regionX;
			this.regionZ = regionZ;
		}

		private final int regionX, regionZ;
		private Node next;
		private final EnderChunk[] regionChunks = new EnderChunk[REGION_BUCKET_SIZE];

	}

	public RegionSet() {
	}

	public RegionSet(Collection<EnderChunk> other) {
		this();
		this.addAll(other);
	}

	public RegionSet(RegionSet other) {
		this();
		for (int x = 0; x < CHUNK_BUCKET_SIZE; x++) {
			Node[] tmp = other.chunkBuckets[x];
			for (int z = 0; z < CHUNK_BUCKET_SIZE; z++) {
				Node otherNode = tmp[z];
				Node thisNode = null;
				for (; otherNode != null; otherNode = otherNode.next) {
					if (thisNode == null)
						thisNode = chunkBuckets[x][z] = new Node(otherNode.regionX, otherNode.regionZ);
					else {
						thisNode.next = new Node(otherNode.regionX, otherNode.regionZ);
						thisNode = thisNode.next;
					}
					System.arraycopy(otherNode.regionChunks, 0, thisNode.regionChunks, 0, REGION_BUCKET_SIZE);
				}
			}
		}
	}

	@Override
	public boolean add(EnderChunk c) {
		int x = c.getX();
		int z = c.getZ();
		
		int rX = calculateRegionPos(x);
		int rZ = calculateRegionPos(z);
		Node prev;
		Node n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)];
		if (n == null) {
			n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)] = new Node(rX, rZ);
		}
		do {
			prev = n;
			if (n.regionX == rX && n.regionZ == rZ) {
				n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] = c;
				return true;
			}
		} while ((n = n.next) != null);

		prev.next = new Node(rX, rZ);
		prev.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] = c;
		return true;
	}

	@Override
	public Iterator<EnderChunk> iterator() {
		return new Iterator<EnderChunk>() {

			private EnderChunk next;
			private int loopSize = Integer.MAX_VALUE;
			private Node node;
			private int chunkX;
			private int chunkZ;
			private boolean hasNext = true;

			private boolean calculateNext() {
				if (hasNext == false) {
					return false;
				}
				mainLoop:
				while (next == null) {
					if (loopSize >= 32 * 32) {
						loopSize = 0;

						if (node != null) {
							node = node.next;
						}
						while (node == null) {
							node = RegionSet.this.chunkBuckets[chunkX][chunkZ];
							if (chunkX >= 15) {
								if (chunkZ >= 15) {
									break mainLoop;
								}
								chunkZ++;
								chunkX = 0;
							} else {
								chunkX++;
							}
						}
					}
					next = node.regionChunks[loopSize];
					loopSize++;
				}
				if (next == null) {
					hasNext = false;
				}
				return next != null;

			}

			@Override
			public boolean hasNext() {
				if (next == null) {
					return calculateNext();
				}
				return true;
			}

			@Override
			public EnderChunk next() {
				if (next == null) {
					if (!calculateNext()) {
						throw new IllegalStateException("No Chunks");
					}
				}
				EnderChunk n = next;
				next = null;
				return n;
			}

			@Override
			public void remove() {
				throw new IllegalStateException("NOPE!");
			}
		};
	}

	@Override
	public int size() {
		int size = 0;
		for (Node[] nodes : chunkBuckets) {
			for (Node singleNode : nodes) {
				if (singleNode == null) {
					continue;
				}
				do {
					for (EnderChunk c : singleNode.regionChunks) {
						if (c != null) {
							size++;
						}
					}
				} while ((singleNode = singleNode.next) != null);
			}
		}
		return size;
	}

	@Override
	public void clear() {
		chunkBuckets = new Node[16][16];
	}

	@Override
	public boolean remove(Object o) {
		EnderChunk c = (EnderChunk) o;
		int x = c.getX();
		int z = c.getZ();
		int rX = calculateRegionPos(x);
		int rZ = calculateRegionPos(z);
		Node n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)];
		if (n != null) {
			do {
				if (n.regionX == rX && n.regionZ == rZ) {
					boolean contains = n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] != null;
					n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] = null;
					return contains;
				}
			} while ((n = n.next) != null);
		}
		return false;
	}

	@Override
	public boolean contains(Object o) {
		EnderChunk c = (EnderChunk) o;
		int x = c.getX();
		int z = c.getZ();
		int rX = calculateRegionPos(x);
		int rZ = calculateRegionPos(z);
		Node n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)];
		if (n != null) {
			do {
				if (n.regionX == rX && n.regionZ == rZ) {
					return n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] != null;
				}
			} while ((n = n.next) != null);
		}
		return false;
	}

	public boolean contains(int x, int z) {
		int rX = calculateRegionPos(x);
		int rZ = calculateRegionPos(z);
		Node n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)];
		if (n != null) {
			do {
				if (n.regionX == rX && n.regionZ == rZ) {
					return n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32] == null;
				}
			} while ((n = n.next) != null);
		}
		return false;
	}

	public EnderChunk get(int x, int z) {
		int rX = calculateRegionPos(x);
		int rZ = calculateRegionPos(z);
		Node n = this.chunkBuckets[maskCordinate(rX)][maskCordinate(rZ)];
		if (n != null) {
			do {
				if (n.regionX == rX && n.regionZ == rZ) {
					EnderChunk chunkF =  n.regionChunks[calculateChunkPos(x) + calculateChunkPos(z) * 32];
					if(chunkF != null && (chunkF.getX() != x || chunkF.getZ() != z)){
						throw new Error("chunkF.getX() != x " + chunkF.getX() + ":"+  x +" AND chunkF.getZ() != z " + chunkF.getZ() +":"+ z);
					}
					return chunkF;
				}
			} while ((n = n.next) != null);
		}
		return null;
	}

	protected static int maskCordinate(int c) {
		return c & 0xF;
	}

	private static int calculateChunkPos(int rawChunkLocation) {
		rawChunkLocation %= 32;
		if (rawChunkLocation < 0) {
			rawChunkLocation += 32;
		}
		return rawChunkLocation;
	}

	protected static int calculateRegionPos(int raw) {
		raw = raw >> 5;
		return raw;
	}
}
