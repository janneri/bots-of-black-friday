export interface ResultValue<V> {
  ok: true
  value: V
}

export interface ResulError<E> {
  ok: false
  error: E
}

export type Result<V, E = any> = ResultValue<V> | ResulError<E>

export function valueResult<V> (value: V): ResultValue<V> {
  return {
    ok: true,
    value
  }
}

export function errorResult<E> (error: E): ResulError<E> {
  return {
    ok: false,
    error
  }
}
