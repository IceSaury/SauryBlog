<template>
  <div class="cyber-input-wrapper">
    <label v-if="label" :for="inputId" class="cyber-input-label">
      {{ label }}
    </label>
    <div class="cyber-input-container">
      <input
        :id="inputId"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        class="cyber-input"
        @input="handleInput"
        @focus="handleFocus"
        @blur="handleBlur"
      />
      <div v-if="isFocused" class="cyber-input-border"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Props {
  modelValue: string
  type?: string
  label?: string
  placeholder?: string
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  placeholder: '',
  disabled: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const isFocused = ref(false)
const inputId = computed(() => `cyber-input-${Math.random().toString(36).substr(2, 9)}`)

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}

const handleFocus = () => {
  isFocused.value = true
}

const handleBlur = () => {
  isFocused.value = false
}
</script>

<style lang="scss" scoped>
.cyber-input-wrapper {
  margin-bottom: 20px;
}

.cyber-input-label {
  display: block;
  margin-bottom: 8px;
  color: $neon-cyan;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.cyber-input-container {
  position: relative;
}

.cyber-input {
  width: 100%;
  background: $bg-secondary;
  border: 1px solid $border-secondary;
  border-radius: 8px;
  padding: 12px 16px;
  color: $text-primary;
  font-size: 14px;
  transition: $transition-base;
  outline: none;

  &:focus {
    border-color: $neon-cyan;
    box-shadow: 0 0 10px rgba($neon-cyan, 0.3);
  }

  &::placeholder {
    color: $text-disabled;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.cyber-input-border {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 8px;
  border: 2px solid transparent;
  background: linear-gradient($bg-secondary, $bg-secondary) padding-box,
    $gradient-cyber border-box;
  pointer-events: none;
  animation: border-flow 3s linear infinite;
}

@keyframes border-flow {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 1;
  }
}
</style>

