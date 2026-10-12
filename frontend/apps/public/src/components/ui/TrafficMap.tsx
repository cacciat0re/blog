import { useEffect, useMemo, useRef, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import * as topojson from 'topojson-client';
import { geoEquirectangular, geoPath } from 'd3-geo';
import { scaleLinear } from 'd3-scale';
import { useTheme } from '../../context/ThemeContext';
import { fetchCountryTraffic } from '../../api/siteStatistics';
import worldTopology from '@/assets/world-110m.json';

const ISO_NUMERIC_TO_ALPHA2: Record<string, string> = {
  '004': 'AF', '008': 'AL', '012': 'DZ', '024': 'AO', '032': 'AR', '036': 'AU',
  '040': 'AT', '050': 'BD', '056': 'BE', '064': 'BT', '068': 'BO', '076': 'BR',
  '100': 'BG', '104': 'MM', '116': 'KH', '120': 'CM', '124': 'CA', '144': 'LK',
  '148': 'TD', '152': 'CL', '156': 'CN', '158': 'TW', '170': 'CO', '180': 'CD',
  '188': 'CR', '191': 'HR', '192': 'CU', '196': 'CY', '203': 'CZ', '208': 'DK',
  '212': 'DM', '214': 'DO', '218': 'EC', '222': 'SV', '231': 'ET', '232': 'ER',
  '233': 'EE', '246': 'FI', '250': 'FR', '262': 'DJ', '266': 'GA', '268': 'GE',
  '270': 'GM', '276': 'DE', '288': 'GH', '300': 'GR', '320': 'GT', '324': 'GN',
  '332': 'HT', '340': 'HN', '344': 'HK', '348': 'HU', '352': 'IS', '356': 'IN',
  '360': 'ID', '364': 'IR', '368': 'IQ', '372': 'IE', '376': 'IL', '380': 'IT',
  '384': 'CI', '388': 'JM', '392': 'JP', '398': 'KZ', '400': 'JO', '404': 'KE',
  '410': 'KR', '414': 'KW', '417': 'KG', '418': 'LA', '422': 'LB', '426': 'LS',
  '428': 'LV', '430': 'LR', '434': 'LY', '440': 'LT', '442': 'LU', '450': 'MG',
  '454': 'MW', '458': 'MY', '462': 'MV', '466': 'ML', '470': 'MT', '478': 'MR',
  '480': 'MU', '484': 'MX', '492': 'MC', '496': 'MN', '504': 'MA', '508': 'MZ',
  '516': 'NA', '524': 'NP', '528': 'NL', '540': 'NC', '548': 'VU', '554': 'NZ',
  '558': 'NI', '562': 'NE', '566': 'NG', '578': 'NO', '586': 'PK', '591': 'PA',
  '598': 'PG', '600': 'PY', '604': 'PE', '608': 'PH', '616': 'PL', '620': 'PT',
  '624': 'GW', '634': 'QA', '642': 'RO', '643': 'RU', '646': 'RW', '682': 'SA',
  '686': 'SN', '694': 'SL', '702': 'SG', '703': 'SK', '704': 'VN', '705': 'SI',
  '706': 'SO', '710': 'ZA', '716': 'ZW', '724': 'ES', '728': 'SS', '729': 'SD',
  '732': 'EH', '740': 'SR', '748': 'SZ', '752': 'SE', '756': 'CH', '760': 'SY',
  '762': 'TJ', '764': 'TH', '768': 'TG', '780': 'TT', '784': 'AE', '788': 'TN',
  '792': 'TR', '795': 'TM', '800': 'UG', '804': 'UA', '807': 'MK', '818': 'EG',
  '826': 'GB', '834': 'TZ', '840': 'US', '854': 'BF', '858': 'UY', '860': 'UZ',
  '862': 'VE', '882': 'WS', '887': 'YE', '894': 'ZM',
};

interface TrafficMapProps { className?: string }
interface HoveredCountry { code: string; visits: number }

export const TrafficMap: React.FC<TrafficMapProps> = ({ className = '' }) => {
  const svgRef = useRef<SVGSVGElement>(null);
  const [dimensions, setDimensions] = useState({ width: 800, height: 400 });
  const [hovered, setHovered] = useState<HoveredCountry | null>(null);
  const { resolved } = useTheme();
  const isDark = resolved === 'dark';

  const { data: trafficData } = useQuery({
    queryKey: ['countryTraffic', 30],
    queryFn: () => fetchCountryTraffic(30),
    staleTime: 60 * 60 * 1000,
  });

  const geoData = useMemo(() => {
    return topojson.feature(worldTopology as any, (worldTopology as any).objects.countries) as unknown as GeoJSON.FeatureCollection;
  }, []);

  useEffect(() => {
    const measure = () => {
      if (svgRef.current?.parentElement) {
        const w = svgRef.current.parentElement.clientWidth;
        setDimensions({ width: w, height: w / 2 });
      }
    };
    measure();
    window.addEventListener('resize', measure);
    return () => window.removeEventListener('resize', measure);
  }, []);

  const noDataFill = isDark ? '#1a1a1a' : '#F5F5F5';
  const strokeColor = isDark ? '#2d2d2d' : '#E0E0E0';

  const colorScale = (() => {
    const visitsList = Object.values(trafficData ?? {});
    if (visitsList.length === 0) return () => noDataFill;
    const maxVisits = Math.max(...visitsList);
    return scaleLinear<string>().domain([0, maxVisits]).range(isDark ? ['#1a1a1a', '#ffffff'] : ['#F5F5F5', '#000000']);
  })();

  const visitsByCode = new Map(Object.entries(trafficData ?? {}));

  const projection = geoEquirectangular()
    .scale(dimensions.width / (2 * Math.PI))
    .translate([dimensions.width / 2, dimensions.height / 2]);
  const pathGenerator = geoPath().projection(projection);

  if (!geoData) {
    return (
      <div className={`flex items-center justify-center py-24 ${className}`}>
        <div className="w-8 h-8 border-2 border-gray-200 dark:border-gray-700 border-t-[#0047FF] rounded-full animate-spin mx-auto mb-4" />
      </div>
    );
  }

  return (
    <div className={className}>
      <svg ref={svgRef} viewBox={`0 0 ${dimensions.width} ${dimensions.height}`} className="w-full" style={{ background: 'transparent' }}>
        {geoData.features.map(feature => {
          const numericId = String(feature.id || '');
          const alpha2 = ISO_NUMERIC_TO_ALPHA2[numericId];
          const visits = alpha2 ? visitsByCode.get(alpha2) : undefined;
          const fill = visits !== undefined ? colorScale(visits) : noDataFill;
          const d = pathGenerator(feature as GeoJSON.Feature);
          if (!d) return null;
          return (
            <path key={numericId} d={d} fill={fill} stroke={strokeColor} strokeWidth={0.5}
              className="transition-colors duration-200 cursor-pointer" style={{ outline: 'none' }}
              onMouseEnter={() => { if (alpha2 && visits !== undefined) setHovered({ code: alpha2, visits }); }}
              onMouseLeave={() => setHovered(null)}
            />
          );
        })}
      </svg>
      <div className="mt-4 flex items-center justify-between border-t-[0.5px] border-gray-200 dark:border-gray-800 pt-3">
        <p className="text-[11px] font-mono text-gray-500 dark:text-gray-400 uppercase tracking-wider">
          {hovered ? <span className="text-black dark:text-white">[{hovered.code}] — {hovered.visits.toLocaleString()} visits</span>
            : <span>Hover over a region to see details</span>}
        </p>
        {trafficData && (
          <p className="text-[10px] font-mono text-gray-400 dark:text-gray-500 uppercase tracking-wider">
            Total: {Object.values(trafficData).reduce((s, v) => s + v, 0).toLocaleString()}
          </p>
        )}
      </div>
    </div>
  );
};