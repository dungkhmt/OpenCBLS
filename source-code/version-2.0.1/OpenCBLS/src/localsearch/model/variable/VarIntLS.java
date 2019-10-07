package localsearch.model.variable;

import localsearch.model.LocalSearchManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Hien Hoang (hienhoang2702@gmail.com)
 */
public class VarIntLS {

    private int value;
    private int oldValue;
    private int min;
    private int max;

    private Set<Integer> domain;
    private Integer[] domainArray;

    private LocalSearchManager localSearchManager;

    private boolean boundedDomain;

    public VarIntLS(int value, int min, int max, LocalSearchManager localSearchManager) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.localSearchManager = localSearchManager;
        boundedDomain = true;
    }

    public VarIntLS(int min, int max, LocalSearchManager localSearchManager, Random rd) {
        this.min = min;
        this.max = max;
        this.localSearchManager = localSearchManager;
        value = rd.nextInt(max - min + 1) + min;
        boundedDomain = true;
    }

    public VarIntLS(int value, Integer[] domainArray, LocalSearchManager localSearchManager) {
        this.value = value;
        this.domainArray = domainArray;
        this.localSearchManager = localSearchManager;
        this.domain = new HashSet<>();
        Collections.addAll(this.domain, domainArray);
        if (!this.domain.contains(value)) {
            throw new RuntimeException("The value of the variable is outside the value domain.");
        }
        boundedDomain = false;
    }

    public VarIntLS(Integer[] domainArray, LocalSearchManager localSearchManager, Random rd) {
        this.domainArray = domainArray;
        this.localSearchManager = localSearchManager;
        this.value = domainArray[rd.nextInt(domainArray.length)];
        this.domain = new HashSet<>();
        Collections.addAll(this.domain, domainArray);
        boundedDomain = false;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getOldValue() {
        return oldValue;
    }

    public boolean isBoundedDomain() {
        return boundedDomain;
    }

    public void setDomainArray(Integer[] domainArray, int value) {
        if (boundedDomain) {
            throw new RuntimeException("Bounded domain not supported domain array.");
        }
        if (localSearchManager.isClosed()) {
            throw new RuntimeException("Cannot set domain array after localSearchManager is closed.");
        }
        this.domainArray = domainArray;
        this.domain.clear();
        Collections.addAll(this.domain, domainArray);
        if (!this.domain.contains(value)) {
            throw new RuntimeException("Value outside bound: " + value);
        }
        this.value = value;
        if (!this.domain.contains(oldValue)) {
            oldValue = value;
        }
    }

    public Integer[] getDomainArray() {
        if (!boundedDomain) {
            return domainArray;
        }
        Integer[] domainArray = new Integer[max - min + 1];
        for (int i = 0; i < domainArray.length; ++i) {
            domainArray[i] = min + i;
        }
        return domainArray;
    }

    public Integer getMin() {
        if (!boundedDomain) {
            throw new RuntimeException("Enumerated domain not supported min.");
        }
        return min;
    }

    public Integer getMax() {
        if (!boundedDomain) {
            throw new RuntimeException("Enumerated domain not supported max.");
        }
        return max;
    }

    public int getDomainSize() {
        if (boundedDomain) {
            return max - min + 1;
        }
        return domainArray.length;
    }

    public void setValue(Integer value) {
        if (!boundedDomain) {
            if (!domain.contains(value)) {
                throw new RuntimeException("Value outside bound: " + value);
            }
            oldValue = this.value;
            this.value = value;
        } else {
            if (value < min || value > max) {
                throw new RuntimeException("Value outside bound. Min: " + min + ", max: " + max + ", actual: " + value);
            }
            oldValue = this.value;
            this.value = value;
        }
    }

    public void setValuePropagate(Integer value) {
        setValue(value);
        if (localSearchManager.isClosed()) {
            localSearchManager.propagate(this);
        }
    }

    public boolean isInDomain(Integer value) {
        if (!boundedDomain) {
            return this.domain.contains(value);
        }
        return value >= min && value <= max;
    }

    public LocalSearchManager getLocalSearchManager() {
        return localSearchManager;
    }
}
