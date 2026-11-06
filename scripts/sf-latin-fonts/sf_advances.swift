// macOS CoreText로 SF 시스템 글꼴의 ASCII 32~126 글자 폭(해당 크기 tracking 포함)을 잰다. usage: swift sf_advances.swift r|m|sb|b <size>
import AppKit
let a=CommandLine.arguments; let w: NSFont.Weight = ["r":.regular,"m":.medium,"sb":.semibold,"b":.bold][a[1]]!
let f=NSFont.systemFont(ofSize:CGFloat(Double(a[2])!),weight:w)
var out=[String]()
for c in 32...126 { let s=String(UnicodeScalar(UInt8(c))); let l=CTLineCreateWithAttributedString(NSAttributedString(string:s,attributes:[.font:f])); out.append(String(format:"%d %.2f",c,CTLineGetTypographicBounds(l,nil,nil,nil))) }
for s in ["·","’","‘","…","~"] { let l=CTLineCreateWithAttributedString(NSAttributedString(string:s,attributes:[.font:f])); out.append(String(format:"%d %.2f",Int(s.unicodeScalars.first!.value),CTLineGetTypographicBounds(l,nil,nil,nil))) }
print(out.joined(separator:"\n"))
