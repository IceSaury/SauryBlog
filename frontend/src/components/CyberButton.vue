<template>
  <button 
    :class="['cyber-btn', `cyber-btn-${type}`, { disabled: disabled }]"
    :disabled="disabled"
    @click="handleClick"
  >
    <span class="cyber-btn-content">
      <slot></slot>
    </span>
    <span class="cyber-btn-glitch" :data-text="buttonText">{{ buttonText }}</span>
  </button>
</template>

<script setup lang="ts">
import { computed, useSlots } from 'vue'

interface Props {
  type?: 'cyan' | 'magenta' | 'purple' | 'yellow'
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'cyan',
  disabled: false,
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const slots = useSlots()

const buttonText = computed(() => {
  return slots.default?.()[0]?.children as string || ''
})

const handleClick = (event: MouseEvent) => {
  if (!props.disabled) {
    emit('click', event)
  }
}
</script>

<style lang="scss" scoped>
.cyber-btn {
  position: relative;
  background: transparent;
  border: 2px solid;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 2px;
  cursor: pointer;
  transition: $transition-base;
  overflow: hidden;
  outline: none;

  &-cyan {
    border-color: $neon-cyan;
    color: $neon-cyan;

    &:hover:not(.disabled) {
      background: rgba($neon-cyan, 0.1);
      box-shadow: $shadow-neon-cyan;
    }
  }

  &-magenta {
    border-color: $neon-magenta;
    color: $neon-magenta;

    &:hover:not(.disabled) {
      background: rgba($neon-magenta, 0.1);
      box-shadow: $shadow-neon-magenta;
    }
  }

  &-purple {
    border-color: $neon-purple;
    color: $neon-purple;

    &:hover:not(.disabled) {
      background: rgba($neon-purple, 0.1);
      box-shadow: $shadow-neon-purple;
    }
  }

  &-yellow {
    border-color: $neon-yellow;
    color: $neon-yellow;

    &:hover:not(.disabled) {
      background: rgba($neon-yellow, 0.1);
      box-shadow: 0 0 10px $neon-yellow, 0 0 20px rgba($neon-yellow, 0.5);
    }
  }

  &.disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  &:active:not(.disabled) {
    transform: scale(0.98);
  }

  &-content {
    position: relative;
    z-index: 1;
  }

  &-glitch {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 100%;
    opacity: 0;
    pointer-events: none;
  }

  &:hover:not(.disabled) &-glitch {
    animation: glitch 0.3s;
  }
}

@keyframes glitch {
  0%, 100% {
    opacity: 0;
    transform: translate(-50%, -50%);
  }
  25% {
    opacity: 1;
    transform: translate(-48%, -48%);
  }
  50% {
    opacity: 1;
    transform: translate(-52%, -52%);
  }
  75% {
    opacity: 1;
    transform: translate(-50%, -50%);
  }
}
</style>

