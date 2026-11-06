"""아이폰 SF 글자 폭(크기별)을 가진 영문·숫자·기호 전용 글꼴을 만든다. LLM 없음.
Pretendard(OFL)에서 ASCII 32~126만 남기고, 글자 폭(hmtx)만 macOS CoreText가 잰 SF 폭(크기별 tracking 포함)으로 바꾼다.
모양과 커닝(GPOS)은 Pretendard 그대로. OFL 예약 이름(Pretendard·Inter·Source)을 쓰지 않으려고 'RH Latin'으로 이름을 바꾼다.
usage (macOS, pip install fonttools): python3 scripts/sf-latin-fonts/gen_latin.py presentation/common/src/main/res/font
"""
import sys, subprocess, os
from fontTools.ttLib import TTFont
from fontTools import subset
from fontTools.pens.t2CharStringPen import T2CharStringPen
from fontTools.pens.transformPen import TransformPen
RES = sys.argv[1]
W = [('regular', 'r', 'Regular'), ('medium', 'm', 'Medium'), ('semibold', 'sb', 'SemiBold'), ('bold', 'b', 'Bold')]
SIZES = [12, 13, 14, 16, 18, 20, 24, 40]
for fn, sw, style in W:
    for s in SIZES:
        out = subprocess.check_output(['swift', os.path.join(os.path.dirname(os.path.abspath(__file__)), 'sf_advances.swift'), sw, str(s)], text=True).split('\n')
        sf = {int(l.split()[0]): float(l.split()[1]) for l in out if l.strip()}
        f = TTFont(os.path.join(RES, f'pretendard_{fn}.otf'))
        opt = subset.Options(); opt.layout_features = ['kern']; opt.name_IDs = []; opt.notdef_outline = True
        opt.hinting = False; opt.desubroutinize = True
        sub = subset.Subsetter(opt); sub.populate(unicodes=range(32, 127)); sub.subset(f)
        upm = f['head'].unitsPerEm; cm = f.getBestCmap(); hm = f['hmtx']
        cff = f['CFF '].cff; td = cff.topDictIndex[0]; cs = td.CharStrings; gs = f.getGlyphSet()
        for c in range(32, 127):
            g = cm[c]; adv, lsb = hm[g]; new = round(sf[c] / s * upm); dx = (new - adv) / 2
            # 늘어난(줄어든) 폭의 절반만큼 모양을 옮겨 글자를 새 폭 가운데 둔다 (SF tracking도 글자 양옆에 고르게 붙는 것과 같게)
            if dx and g != 'space':
                pen = T2CharStringPen(new, gs); gs[g].draw(TransformPen(pen, (1, 0, 0, 1, dx, 0)))
                ch = pen.getCharString(private=cs[g].private, globalSubrs=cs[g].globalSubrs); cs[g] = ch
                lsb = round(lsb + dx)
            hm[g] = (new, lsb)
        fam = f'RH Latin {s}'
        nm = f['name']; nm.names = []
        for nid, val in [(0, 'Derived from Pretendard, Copyright (c) 2021 Kil Hyung-jin. SIL Open Font License 1.1'),
                         (1, fam), (2, style), (4, f'{fam} {style}'), (6, f'RHLatin{s}-{style}'),
                         (13, 'This Font Software is licensed under the SIL Open Font License, Version 1.1.'),
                         (14, 'https://openfontlicense.org')]:
            nm.setName(val, nid, 3, 1, 0x409)
        f['CFF '].cff.fontNames = [f'RHLatin{s}-{style}']
        f.save(os.path.join(RES, f'rh_latin_{fn}_{s}.otf'))
print('done')
