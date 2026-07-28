interface BrandLogoProps {
  compact?: boolean
  large?: boolean
  logoOnly?: boolean
}

const logoSource = '/WhatsApp%20Image%202026-07-27%20at%2016.13.58.jpeg'

export function BrandLogo({ compact = false, large = false, logoOnly = false }: BrandLogoProps) {
  return (
    <div className="flex items-center gap-3">
      <img
        src={logoSource}
        alt="67 PAY"
        className={`${compact ? 'h-9 w-9 rounded-xl' : large ? 'h-40 w-40 rounded-[46px]' : 'h-14 w-14 rounded-[18px]'} object-cover shadow-brand`}
      />
      {!logoOnly && <div className={compact ? 'hidden sm:block' : ''}>
        <p className={`${large ? 'text-2xl' : 'text-sm'} font-extrabold tracking-[0.18em] text-white`}>67 PAY</p>
      </div>}
    </div>
  )
}
