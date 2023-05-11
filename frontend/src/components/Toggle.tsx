import React from 'react'

export function Toggle ({ id, label, checked, onChange }: {
  id: string
  label: string | JSX.Element
  checked: boolean
  onChange: (val: boolean) => void
}): JSX.Element {
  /* Original HTML / Tailwind CSS Source: https://tailwindcomponents.com/component/toggle-switch */
  return <div>
    <div
      className="relative inline-block w-10 mr-2 align-middle select-none transition duration-200 ease-in"
    >
      <input
        type="checkbox"
        name={id}
        id={id}
        checked={checked}
        className="toggle-checkbox absolute block w-6 h-6 rounded-full bg-white border-4 appearance-none cursor-pointer"
        onChange={(e) => {
          onChange(e.target.checked)
        }}
      />
      <label
        htmlFor={id}
        className="toggle-label block overflow-hidden h-6 rounded-full bg-gray-300 cursor-pointer"></label>
    </div>
    <label htmlFor={id} className="text-[0.5rem]">{label}</label>
  </div>
}
