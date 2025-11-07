<template>
  <div :class="['cyber-card-wrapper', { hoverable: hoverable }]" @click="handleClick">
    <div class="cyber-card-border"></div>
    <div class="cyber-card-content">
      <slot></slot>
    </div>
    <div v-if="glow" class="cyber-card-glow"></div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  hoverable?: boolean
  glow?: boolean
}

withDefaults(defineProps<Props>(), {
  hoverable: true,
  glow: true,
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const handleClick = (event: MouseEvent) => {
  emit('click', event)
}
</script>

<style lang="scss" scoped>
.cyber-card-wrapper {
  position: relative;
  background: $bg-card;
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 24px;
  transition: $transition-base;

  &.hoverable {
    cursor: pointer;

    &:hover {
      transform: translateY(-4px);

      .cyber-card-border {
        opacity: 1;
      }

      .cyber-card-glow {
        opacity: 1;
      }
    }
  }
}

.cyber-card-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 12px;
  border: 1px solid $border-secondary;
  opacity: 0.5;
  transition: $transition-base;
  pointer-events: none;

  .cyber-card-wrapper:hover & {
    border-color: $neon-cyan;
    box-shadow: $shadow-neon-cyan;
  }
}

.cyber-card-content {
  position: relative;
  z-index: 5;
}

.cyber-card-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle,
    rgba($neon-cyan, 0.1) 0%,
    transparent 70%
  );
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  animation: rotate 10s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>

