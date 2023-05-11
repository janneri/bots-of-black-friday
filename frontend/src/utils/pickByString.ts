export function toHashCode (str: string): number {
  let hash = 0

  for (let i = 0; i < str.length; i++) {
    hash += str.charCodeAt(i)
    hash = Number.isSafeInteger(hash) ? hash : 0
  }

  return hash
}

export function pickByString<T> (str: string, arr: readonly T[]): T {
  const hash = toHashCode(str)
  const mod = hash % arr.length

  return arr[mod]
}
