/**
 * Trunca um datetime ISO ("2026-07-15T00:00:00") para o formato de data pura
 * "yyyy-MM-dd" exigido pelo `TjDatePicker.writeValue()` (usa `date-fns parse`
 * estrito nesse formato) — sem essa truncagem, o picker recebe uma string com
 * "T" no meio, o parse falha e o componente quebra com "Invalid Date".
 */
export function toDateOnly(
  value: string | null | undefined
): string | undefined {
  return value ? value.substring(0, 10) : undefined;
}
