package org.dynmap.blockscan.statehandlers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class ForgeStateContainer extends StateContainer {

	public ForgeStateContainer(Block blk, Set<String> renderprops, Map<String, List<String>> propMap) {
		List<BlockState> bsl = blk.getStateDefinition().getPossibleStates();
		BlockState defstate = blk.defaultBlockState();
		if (renderprops == null) {
			renderprops = new HashSet<String>();
			for (String pn : propMap.keySet()) {
				renderprops.add(pn);
			}
		}
		// Build table of render properties and valid values
		for (String pn : propMap.keySet()) {
			if (renderprops.contains(pn) == false) {
				continue;
			}
			this.renderProperties.put(pn, propMap.get(pn));
		}
		
		this.defStateIndex = 0;
		int idx = 0;
		for (BlockState bs : bsl) {
			ImmutableMap.Builder<String,String> bld = ImmutableMap.builder();
			for (Property<?> ent : bs.getProperties()) {
				String pn = ent.getName();
				if (renderprops.contains(pn)) {	// If valid render property
					Comparable<?> v = bs.getValue(ent);
					//if (v instanceof IStringSerializable) {
					//	v = ((IStringSerializable)v).toString();
					//}
					bld.put(pn, v.toString());
				}
			}
			StateRec sr = new StateRec(idx, bld.build());
			int prev_sr = records.indexOf(sr);
			if (prev_sr < 0) {
				if (bs.equals(defstate)) {
					this.defStateIndex = records.size();
				}
				records.add(sr);
			}
			else {
				StateRec prev = records.get(prev_sr);
				if (prev.hasMeta(idx) == false) {
					sr = new StateRec(prev, idx);
					records.set(prev_sr, sr);
					if (bs.equals(defstate)) {
						this.defStateIndex = prev_sr;
					}
				}
			}
			idx++;
		}
		// Check for well-known block types
		if (blk instanceof LeavesBlock) {
		    type = WellKnownBlockClasses.LEAVES;
		}
		else if (blk instanceof CropBlock) {
            type = WellKnownBlockClasses.CROPS;
		}
		else if (blk instanceof FlowerBlock) {
		    type = WellKnownBlockClasses.FLOWER;
		}
		else if (blk instanceof TallGrassBlock) {
            type = WellKnownBlockClasses.TALLGRASS;
		}
		else if (blk instanceof VineBlock) {
		    type = WellKnownBlockClasses.VINES;
		}
        else if (blk instanceof BushBlock) {
            type = WellKnownBlockClasses.BUSH;
        }
        else if (blk instanceof GrassBlock) {
            type = WellKnownBlockClasses.GRASS;
        }
        else if (blk instanceof LiquidBlock) {
            type = WellKnownBlockClasses.LIQUID;
        }
	}
}
