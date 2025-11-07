<template>
  <span :class="['cyber-tag', `cyber-tag-${color}`]">
    <slot></slot>
  </span>
</template>

<script setup lang="ts">
interface Props {
  color?: 'cyan' | 'magenta' | 'purple' | 'yellow' | 'green'
}

withDefaults(defineProps<Props>(), {
  color: 'cyan',
})
</script>

<style lang="scss" scoped>
.cyber-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  margin: 4px;
  transition: $transition-fast;
  border: 1px solid;

  &-cyan {
    background: rgba($neon-cyan, 0.1);
    border-color: $neon-cyan;
    color: $neon-cyan;

    &:hover {
      background: rgba($neon-cyan, 0.2);
      box-shadow: 0 0 10px $neon-cyan;
    }
  }

  &-magenta {
    background: rgba($neon-magenta, 0.1);
    border-color: $neon-magenta;
    color: $neon-magenta;

    &:hover {
      background: rgba($neon-magenta, 0.2);
      box-shadow: 0 0 10px $neon-magenta;
    }
  }

  &-purple {
    background: rgba($neon-purple, 0.1);
    border-color: $neon-purple;
    color: $neon-purple;

    &:hover {
      background: rgba($neon-purple, 0.2);
      box-shadow: 0 0 10px $neon-purple;
    }
  }

  &-yellow {
    background: rgba($neon-yellow, 0.1);
    border-color: $neon-yellow;
    color: $neon-yellow;

    &:hover {
      background: rgba($neon-yellow, 0.2);
      box-shadow: 0 0 10px $neon-yellow;
    }
  }

  &-green {
    background: rgba($neon-green, 0.1);
    border-color: $neon-green;
    color: $neon-green;

    &:hover {
      background: rgba($neon-green, 0.2);
      box-shadow: 0 0 10px $neon-green;
    }
  }
}
</style>

