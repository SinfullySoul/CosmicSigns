package com.github.sinfullysoul.mixins;

import com.badlogic.gdx.utils.Array;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.NoSuchElementException;

@Mixin(Array.ArrayIterator.class)
public class ArrayIteratorMixin<T> {


    @Shadow private int index;
    @Shadow @Final private Array<T> array;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean hasNext() {
        return this.index < this.array.size;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public T next() {
        if (this.index >= this.array.size) {
            throw new NoSuchElementException(String.valueOf(this.index));
        } else {
            return this.array.items[this.index++];
        }
    }
}
