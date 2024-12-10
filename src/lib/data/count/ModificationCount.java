package lib.data.count;
import lib.util.Base;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ModificationCount {

    //Content <Referencebase,Modification,Count>
    private Map<Base, Map<String, Integer>> modCount;

    public ModificationCount(final ModificationCount modCount) {
        this.modCount = deepCopyModCount(modCount.modCount);
    }

    public ModificationCount() {
        this.modCount = new EnumMap<>(Base.class);
    }


    public ModificationCount(final Map<Base, Map<String, Integer>> modCount) {
        this.modCount = modCount;
    }


    //set modCount by giving complete nested map
    public void setModCount(Map<Base, Map<String, Integer>> modCount) {
        this.modCount = modCount;
    }


    //set modCount by giving base and modification
    public void setModCount(Base base, String mod) {
        //get inner map to base parameter or create new one if not existent
        modCount.computeIfAbsent(base, x -> new HashMap<>())
                //if inner map contains key mod, count one up, otherwise set 1
                .merge(mod, 1, Integer::sum);
    }


    //get all modifications with belonging bases
    public Map<Base, Map<String, Integer>> getModCount() {
        return this.modCount;
    }


    //get modifications to specified base
    public Map<String, Integer> getModCount(final Base base) {
        return this.modCount.get(base);
    }


    //get modifications of one specified kind
    public Map<Base, Map<String, Integer>> getModCount(final String mod) {
        //new outer map
        Map<Base, Map<String, Integer>> modCountToMod = new EnumMap<>(Base.class);
        //iterate over outer map to search for key of inner map
        for (Map.Entry<Base, Map<String, Integer>> entry : this.modCount.entrySet()) {
            Base outerKey = entry.getKey();
            Map<String, Integer> innerMap = entry.getValue();
            //if mod is in inner map
            if (innerMap.containsKey(mod)) {
                //create or expand inner map, depending if base-key is already existent
                modCountToMod.computeIfAbsent(outerKey, k -> new HashMap<>())
                        .put(mod, innerMap.get(mod));
            }
        }
        return modCountToMod;
    }


    public Map<Base, Map<String, Integer>> getModCount(final Base base, final String mod) {
        Map<Base, Map<String, Integer>> modCountFromBaseAndMod = new EnumMap<>(Base.class);
        //go through outer map and find entries where key of outer and inner map match parameters
        for (Map.Entry<Base, Map<String, Integer>> entry : modCount.entrySet()) {
            if (entry.getKey() == base && entry.getValue().containsKey(mod)) {
                //create new map containing just the pairs where mod is key
                Map<String, Integer> filteredInnerMap = new HashMap<>();
                filteredInnerMap.put(mod, entry.getValue().get(mod));
                //add filtered combination map to new map
                modCountFromBaseAndMod.put(base, filteredInnerMap);
            }
        }
        return modCountFromBaseAndMod;
    }


    //returns count value to specified base and mod
    public Integer getModCountInteger(final Base base, final String mod) {
        return this.modCount.get(base).get(mod);
    }

    public ModificationCount copy() {
        return new ModificationCount(this);
    }

    public static ModificationCount create() {
        return new ModificationCount();
    }

    public static String modCountToString(Map<Base, Map<String, Integer>> modCount) {
        final StringBuilder sb = new StringBuilder();

        //make map to string
        sb.append("Modifications: ");
        for(Map.Entry<Base, Map<String, Integer>> entry : modCount.entrySet()) {
            for (Map.Entry<String, Integer> innerEntry : entry.getValue().entrySet()) {
                sb.append(entry.getKey());
                sb.append(",");
                sb.append(innerEntry.getKey());
                sb.append(",");
                sb.append(innerEntry.getValue());
            }
        }

        return sb.toString();
    }


    //method to deep-copy the modCount variable
    private Map<Base, Map<String, Integer>> deepCopyModCount(Map<Base, Map<String, Integer>> originalModCount) {
        Map<Base, Map<String, Integer>> copyModCount = new EnumMap<>(Base.class);
        for (Map.Entry<Base, Map<String, Integer>> entry : originalModCount.entrySet()) {
            Map<String, Integer> innerCopy = new HashMap<>(entry.getValue());
            copyModCount.put(entry.getKey(), innerCopy);
        }
        return copyModCount;
    }
}
