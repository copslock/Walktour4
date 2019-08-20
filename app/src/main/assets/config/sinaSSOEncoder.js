var navigator = navigator || {};
(function () {
    //this.appName = "Microsoft Internet Explorer";
    this.appName = "Netscape";
}).call(navigator);

var sinaSSOEncoder = sinaSSOEncoder || {};
(function () {
    var n = 0;
    var o = 8;
    this.hex_sha1 = function (s) {
        return A(p(z(s), s.length * o))
    };
    var p = function (x, f) {
        x[f >> 5] |= 0x80 << (24 - f % 32);
        x[((f + 64 >> 9) << 4) + 15] = f;
        var w = Array(80);
        var a = 1732584193;
        var b = -271733879;
        var c = -1732584194;
        var d = 271733878;
        var e = -1009589776;
        for (var i = 0; i < x.length; i += 16) {
            var g = a;
            var h = b;
            var k = c;
            var l = d;
            var m = e;
            for (var j = 0; j < 80; j++) {
                if (j < 16) w[j] = x[i + j];
                else w[j] = v(w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16], 1);
                var t = u(u(v(a, 5), q(j, b, c, d)), u(u(e, w[j]), r(j)));
                e = d;
                d = c;
                c = v(b, 30);
                b = a;
                a = t
            }
            a = u(a, g);
            b = u(b, h);
            c = u(c, k);
            d = u(d, l);
            e = u(e, m)
        }
        return Array(a, b, c, d, e)
    };
    var q = function (t, b, c, d) {
        if (t < 20) return (b & c) | ((~b) & d);
        if (t < 40) return b ^ c ^ d;
        if (t < 60) return (b & c) | (b & d) | (c & d);
        return b ^ c ^ d
    };
    var r = function (t) {
        return (t < 20) ? 1518500249 : (t < 40) ? 1859775393 : (t < 60) ? -1894007588 : -899497514
    };
    var u = function (x, y) {
        var a = (x & 0xFFFF) + (y & 0xFFFF);
        var b = (x >> 16) + (y >> 16) + (a >> 16);
        return (b << 16) | (a & 0xFFFF)
    };
    var v = function (a, b) {
        return (a << b) | (a >>> (32 - b))
    };
    var z = function (a) {
        var b = Array();
        var c = (1 << o) - 1;
        for (var i = 0; i < a.length * o; i += o) b[i >> 5] |= (a.charCodeAt(i / o) & c) << (24 - i % 32);
        return b
    };
    var A = function (a) {
        var b = n ? "0123456789ABCDEF" : "0123456789abcdef";
        var c = "";
        for (var i = 0; i < a.length * 4; i++) {
            c += b.charAt((a[i >> 2] >> ((3 - i % 4) * 8 + 4)) & 0xF) + b.charAt((a[i >> 2] >> ((3 - i % 4) * 8)) & 0xF)
        }
        return c
    };
    this.base64 = {
        encode: function (a) {
            a = "" + a;
            if (a == "") return "";
            var b = '';
            var c, chr2, chr3 = '';
            var d, enc2, enc3, enc4 = '';
            var i = 0;
            do {
                c = a.charCodeAt(i++);
                chr2 = a.charCodeAt(i++);
                chr3 = a.charCodeAt(i++);
                d = c >> 2;
                enc2 = ((c & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;
                if (isNaN(chr2)) {
                    enc3 = enc4 = 64
                } else if (isNaN(chr3)) {
                    enc4 = 64
                }
                b = b + this._keys.charAt(d) + this._keys.charAt(enc2) + this._keys.charAt(enc3) + this._keys.charAt(enc4);
                c = chr2 = chr3 = '';
                d = enc2 = enc3 = enc4 = ''
            } while (i < a.length);
            return b
        },
        _keys: 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/='
    }
}).call(sinaSSOEncoder);
(function () {
    var o;
    var u = 0xdeadbeefcafe;
    var A = ((u & 0xffffff) == 0xefcafe);

    function BigInteger(a, b, c) {
        if (a != null) if ("number" == typeof a) this.fromNumber(a, b, c);
            else if (b == null && "string" != typeof a) this.fromString(a, 256);
        else this.fromString(a, b)
    }
    function nbi() {
        return new BigInteger(null)
    }
    function am1(i, x, w, j, c, n) {
        while (--n >= 0) {
            var v = x * this[i++] + w[j] + c;
            c = Math.floor(v / 0x4000000);
            w[j++] = v & 0x3ffffff
        }
        return c
    }
    function am2(i, x, w, j, c, n) {
        var a = x & 0x7fff,
            xh = x >> 15;
        while (--n >= 0) {
            var l = this[i] & 0x7fff;
            var h = this[i++] >> 15;
            var m = xh * l + h * a;
            l = a * l + ((m & 0x7fff) << 15) + w[j] + (c & 0x3fffffff);
            c = (l >>> 30) + (m >>> 15) + xh * h + (c >>> 30);
            w[j++] = l & 0x3fffffff
        }
        return c
    }
    function am3(i, x, w, j, c, n) {
        var a = x & 0x3fff,
            xh = x >> 14;
        while (--n >= 0) {
            var l = this[i] & 0x3fff;
            var h = this[i++] >> 14;
            var m = xh * l + h * a;
            l = a * l + ((m & 0x3fff) << 14) + w[j] + c;
            c = (l >> 28) + (m >> 14) + xh * h;
            w[j++] = l & 0xfffffff
        }
        return c
    }
    if (A && (navigator.appName == "Microsoft Internet Explorer")) {
        BigInteger.prototype.am = am2;
        o = 30
    } else if (A && (navigator.appName != "Netscape")) {
        BigInteger.prototype.am = am1;
        o = 26
    } else {
        BigInteger.prototype.am = am3;
        o = 28
    }
    BigInteger.prototype.DB = o;
    BigInteger.prototype.DM = ((1 << o) - 1);
    BigInteger.prototype.DV = (1 << o);
    var B = 52;
    BigInteger.prototype.FV = Math.pow(2, B);
    BigInteger.prototype.F1 = B - o;
    BigInteger.prototype.F2 = 2 * o - B;
    var C = "0123456789abcdefghijklmnopqrstuvwxyz";
    var D = new Array();
    var F, vv;
    F = "0".charCodeAt(0);
    for (vv = 0; vv <= 9; ++vv) D[F++] = vv;
    F = "a".charCodeAt(0);
    for (vv = 10; vv < 36; ++vv) D[F++] = vv;
    F = "A".charCodeAt(0);
    for (vv = 10; vv < 36; ++vv) D[F++] = vv;

    function int2char(n) {
        return C.charAt(n)
    }
    function intAt(s, i) {
        var c = D[s.charCodeAt(i)];
        return (c == null) ? -1 : c
    }
    function bnpCopyTo(r) {
        for (var i = this.t - 1; i >= 0; --i) r[i] = this[i];
        r.t = this.t;
        r.s = this.s
    }
    function bnpFromInt(x) {
        this.t = 1;
        this.s = (x < 0) ? -1 : 0;
        if (x > 0) this[0] = x;
        else if (x < -1) this[0] = x + DV;
        else this.t = 0
    }
    function nbv(i) {
        var r = nbi();
        r.fromInt(i);
        return r
    }
    function bnpFromString(s, b) {
        var k;
        if (b == 16) k = 4;
        else if (b == 8) k = 3;
        else if (b == 256) k = 8;
        else if (b == 2) k = 1;
        else if (b == 32) k = 5;
        else if (b == 4) k = 2;
        else {
            this.fromRadix(s, b);
            return
        }
        this.t = 0;
        this.s = 0;
        var i = s.length,
            mi = false,
            sh = 0;
        while (--i >= 0) {
            var x = (k == 8) ? s[i] & 0xff : intAt(s, i);
            if (x < 0) {
                if (s.charAt(i) == "-") mi = true;
                continue
            }
            mi = false;
            if (sh == 0) this[this.t++] = x;
            else if (sh + k > this.DB) {
                this[this.t - 1] |= (x & ((1 << (this.DB - sh)) - 1)) << sh;
                this[this.t++] = (x >> (this.DB - sh))
            } else this[this.t - 1] |= x << sh;
            sh += k;
            if (sh >= this.DB) sh -= this.DB
        }
        if (k == 8 && (s[0] & 0x80) != 0) {
            this.s = -1;
            if (sh > 0) this[this.t - 1] |= ((1 << (this.DB - sh)) - 1) << sh
        }
        this.clamp();
        if (mi) BigInteger.ZERO.subTo(this, this)
    }
    function bnpClamp() {
        var c = this.s & this.DM;
        while (this.t > 0 && this[this.t - 1] == c)--this.t
    }
    function bnToString(b) {
        if (this.s < 0) return "-" + this.negate().toString(b);
        var k;
        if (b == 16) k = 4;
        else if (b == 8) k = 3;
        else if (b == 2) k = 1;
        else if (b == 32) k = 5;
        else if (b == 4) k = 2;
        else return this.toRadix(b);
        var a = (1 << k) - 1,
            d, m = false,
            r = "",
            i = this.t;
        var p = this.DB - (i * this.DB) % k;
        if (i-- > 0) {
            if (p < this.DB && (d = this[i] >> p) > 0) {
                m = true;
                r = int2char(d)
            }
            while (i >= 0) {
                if (p < k) {
                    d = (this[i] & ((1 << p) - 1)) << (k - p);
                    d |= this[--i] >> (p += this.DB - k)
                } else {
                    d = (this[i] >> (p -= k)) & a;
                    if (p <= 0) {
                        p += this.DB;
                        --i
                    }
                } if (d > 0) m = true;
                if (m) r += int2char(d)
            }
        }
        return m ? r : "0"
    }
    function bnNegate() {
        var r = nbi();
        BigInteger.ZERO.subTo(this, r);
        return r
    }
    function bnAbs() {
        return (this.s < 0) ? this.negate() : this
    }
    function bnCompareTo(a) {
        var r = this.s - a.s;
        if (r != 0) return r;
        var i = this.t;
        r = i - a.t;
        if (r != 0) return r;
        while (--i >= 0) if ((r = this[i] - a[i]) != 0) return r;
        return 0
    }
    function nbits(x) {
        var r = 1,
            t;
        if ((t = x >>> 16) != 0) {
            x = t;
            r += 16
        }
        if ((t = x >> 8) != 0) {
            x = t;
            r += 8
        }
        if ((t = x >> 4) != 0) {
            x = t;
            r += 4
        }
        if ((t = x >> 2) != 0) {
            x = t;
            r += 2
        }
        if ((t = x >> 1) != 0) {
            x = t;
            r += 1
        }
        return r
    }
    function bnBitLength() {
        if (this.t <= 0) return 0;
        return this.DB * (this.t - 1) + nbits(this[this.t - 1] ^ (this.s & this.DM))
    }
    function bnpDLShiftTo(n, r) {
        var i;
        for (i = this.t - 1; i >= 0; --i) r[i + n] = this[i];
        for (i = n - 1; i >= 0; --i) r[i] = 0;
        r.t = this.t + n;
        r.s = this.s
    }
    function bnpDRShiftTo(n, r) {
        for (var i = n; i < this.t; ++i) r[i - n] = this[i];
        r.t = Math.max(this.t - n, 0);
        r.s = this.s
    }
    function bnpLShiftTo(n, r) {
        var a = n % this.DB;
        var b = this.DB - a;
        var d = (1 << b) - 1;
        var e = Math.floor(n / this.DB),
            c = (this.s << a) & this.DM,
            i;
        for (i = this.t - 1; i >= 0; --i) {
            r[i + e + 1] = (this[i] >> b) | c;
            c = (this[i] & d) << a
        }
        for (i = e - 1; i >= 0; --i) r[i] = 0;
        r[e] = c;
        r.t = this.t + e + 1;
        r.s = this.s;
        r.clamp()
    }
    function bnpRShiftTo(n, r) {
        r.s = this.s;
        var a = Math.floor(n / this.DB);
        if (a >= this.t) {
            r.t = 0;
            return
        }
        var b = n % this.DB;
        var c = this.DB - b;
        var d = (1 << b) - 1;
        r[0] = this[a] >> b;
        for (var i = a + 1; i < this.t; ++i) {
            r[i - a - 1] |= (this[i] & d) << c;
            r[i - a] = this[i] >> b
        }
        if (b > 0) r[this.t - a - 1] |= (this.s & d) << c;
        r.t = this.t - a;
        r.clamp()
    }
    function bnpSubTo(a, r) {
        var i = 0,
            c = 0,
            m = Math.min(a.t, this.t);
        while (i < m) {
            c += this[i] - a[i];
            r[i++] = c & this.DM;
            c >>= this.DB
        }
        if (a.t < this.t) {
            c -= a.s;
            while (i < this.t) {
                c += this[i];
                r[i++] = c & this.DM;
                c >>= this.DB
            }
            c += this.s
        } else {
            c += this.s;
            while (i < a.t) {
                c -= a[i];
                r[i++] = c & this.DM;
                c >>= this.DB
            }
            c -= a.s
        }
        r.s = (c < 0) ? -1 : 0;
        if (c < -1) r[i++] = this.DV + c;
        else if (c > 0) r[i++] = c;
        r.t = i;
        r.clamp()
    }
    function bnpMultiplyTo(a, r) {
        var x = this.abs(),
            y = a.abs();
        var i = x.t;
        r.t = i + y.t;
        while (--i >= 0) r[i] = 0;
        for (i = 0; i < y.t; ++i) r[i + x.t] = x.am(0, y[i], r, i, 0, x.t);
        r.s = 0;
        r.clamp();
        if (this.s != a.s) BigInteger.ZERO.subTo(r, r)
    }
    function bnpSquareTo(r) {
        var x = this.abs();
        var i = r.t = 2 * x.t;
        while (--i >= 0) r[i] = 0;
        for (i = 0; i < x.t - 1; ++i) {
            var c = x.am(i, x[i], r, 2 * i, 0, 1);
            if ((r[i + x.t] += x.am(i + 1, 2 * x[i], r, 2 * i + 1, c, x.t - i - 1)) >= x.DV) {
                r[i + x.t] -= x.DV;
                r[i + x.t + 1] = 1
            }
        }
        if (r.t > 0) r[r.t - 1] += x.am(i, x[i], r, 2 * i, 0, 1);
        r.s = 0;
        r.clamp()
    }
    function bnpDivRemTo(m, q, r) {
        var a = m.abs();
        if (a.t <= 0) return;
        var b = this.abs();
        if (b.t < a.t) {
            if (q != null) q.fromInt(0);
            if (r != null) this.copyTo(r);
            return
        }
        if (r == null) r = nbi();
        var y = nbi(),
            ts = this.s,
            ms = m.s;
        var c = this.DB - nbits(a[a.t - 1]);
        if (c > 0) {
            a.lShiftTo(c, y);
            b.lShiftTo(c, r)
        } else {
            a.copyTo(y);
            b.copyTo(r)
        }
        var d = y.t;
        var f = y[d - 1];
        if (f == 0) return;
        var g = f * (1 << this.F1) + ((d > 1) ? y[d - 2] >> this.F2 : 0);
        var h = this.FV / g,
            d2 = (1 << this.F1) / g,
            e = 1 << this.F2;
        var i = r.t,
            j = i - d,
            t = (q == null) ? nbi() : q;
        y.dlShiftTo(j, t);
        if (r.compareTo(t) >= 0) {
            r[r.t++] = 1;
            r.subTo(t, r)
        }
        BigInteger.ONE.dlShiftTo(d, t);
        t.subTo(y, y);
        while (y.t < d) y[y.t++] = 0;
        while (--j >= 0) {
            var k = (r[--i] == f) ? this.DM : Math.floor(r[i] * h + (r[i - 1] + e) * d2);
            if ((r[i] += y.am(0, k, r, j, 0, d)) < k) {
                y.dlShiftTo(j, t);
                r.subTo(t, r);
                while (r[i] < --k) r.subTo(t, r)
            }
        }
        if (q != null) {
            r.drShiftTo(d, q);
            if (ts != ms) BigInteger.ZERO.subTo(q, q)
        }
        r.t = d;
        r.clamp();
        if (c > 0) r.rShiftTo(c, r);
        if (ts < 0) BigInteger.ZERO.subTo(r, r)
    }
    function bnMod(a) {
        var r = nbi();
        this.abs().divRemTo(a, null, r);
        if (this.s < 0 && r.compareTo(BigInteger.ZERO) > 0) a.subTo(r, r);
        return r
    }
    function Classic(m) {
        this.m = m
    }
    function cConvert(x) {
        if (x.s < 0 || x.compareTo(this.m) >= 0) return x.mod(this.m);
        else return x
    }
    function cRevert(x) {
        return x
    }
    function cReduce(x) {
        x.divRemTo(this.m, null, x)
    }
    function cMulTo(x, y, r) {
        x.multiplyTo(y, r);
        this.reduce(r)
    }
    function cSqrTo(x, r) {
        x.squareTo(r);
        this.reduce(r)
    }
    Classic.prototype.convert = cConvert;
    Classic.prototype.revert = cRevert;
    Classic.prototype.reduce = cReduce;
    Classic.prototype.mulTo = cMulTo;
    Classic.prototype.sqrTo = cSqrTo;

    function bnpInvDigit() {
        if (this.t < 1) return 0;
        var x = this[0];
        if ((x & 1) == 0) return 0;
        var y = x & 3;
        y = (y * (2 - (x & 0xf) * y)) & 0xf;
        y = (y * (2 - (x & 0xff) * y)) & 0xff;
        y = (y * (2 - (((x & 0xffff) * y) & 0xffff))) & 0xffff;
        y = (y * (2 - x * y % this.DV)) % this.DV;
        return (y > 0) ? this.DV - y : -y
    }
    function Montgomery(m) {
        this.m = m;
        this.mp = m.invDigit();
        this.mpl = this.mp & 0x7fff;
        this.mph = this.mp >> 15;
        this.um = (1 << (m.DB - 15)) - 1;
        this.mt2 = 2 * m.t
    }
    function montConvert(x) {
        var r = nbi();
        x.abs().dlShiftTo(this.m.t, r);
        r.divRemTo(this.m, null, r);
        if (x.s < 0 && r.compareTo(BigInteger.ZERO) > 0) this.m.subTo(r, r);
        return r
    }
    function montRevert(x) {
        var r = nbi();
        x.copyTo(r);
        this.reduce(r);
        return r
    }
    function montReduce(x) {
        while (x.t <= this.mt2) x[x.t++] = 0;
        for (var i = 0; i < this.m.t; ++i) {
            var j = x[i] & 0x7fff;
            var a = (j * this.mpl + (((j * this.mph + (x[i] >> 15) * this.mpl) & this.um) << 15)) & x.DM;
            j = i + this.m.t;
            x[j] += this.m.am(0, a, x, i, 0, this.m.t);
            while (x[j] >= x.DV) {
                x[j] -= x.DV;
                x[++j]++
            }
        }
        x.clamp();
        x.drShiftTo(this.m.t, x);
        if (x.compareTo(this.m) >= 0) x.subTo(this.m, x)
    }
    function montSqrTo(x, r) {
        x.squareTo(r);
        this.reduce(r)
    }
    function montMulTo(x, y, r) {
        x.multiplyTo(y, r);
        this.reduce(r)
    }
    Montgomery.prototype.convert = montConvert;
    Montgomery.prototype.revert = montRevert;
    Montgomery.prototype.reduce = montReduce;
    Montgomery.prototype.mulTo = montMulTo;
    Montgomery.prototype.sqrTo = montSqrTo;

    function bnpIsEven() {
        return ((this.t > 0) ? (this[0] & 1) : this.s) == 0
    }
    function bnpExp(e, z) {
        if (e > 0xffffffff || e < 1) return BigInteger.ONE;
        var r = nbi(),
            r2 = nbi(),
            g = z.convert(this),
            i = nbits(e) - 1;
        g.copyTo(r);
        while (--i >= 0) {
            z.sqrTo(r, r2);
            if ((e & (1 << i)) > 0) z.mulTo(r2, g, r);
            else {
                var t = r;
                r = r2;
                r2 = t
            }
        }
        return z.revert(r)
    }
    function bnModPowInt(e, m) {
        var z;
        if (e < 256 || m.isEven()) z = new Classic(m);
        else z = new Montgomery(m);
        return this.exp(e, z)
    }
    BigInteger.prototype.copyTo = bnpCopyTo;
    BigInteger.prototype.fromInt = bnpFromInt;
    BigInteger.prototype.fromString = bnpFromString;
    BigInteger.prototype.clamp = bnpClamp;
    BigInteger.prototype.dlShiftTo = bnpDLShiftTo;
    BigInteger.prototype.drShiftTo = bnpDRShiftTo;
    BigInteger.prototype.lShiftTo = bnpLShiftTo;
    BigInteger.prototype.rShiftTo = bnpRShiftTo;
    BigInteger.prototype.subTo = bnpSubTo;
    BigInteger.prototype.multiplyTo = bnpMultiplyTo;
    BigInteger.prototype.squareTo = bnpSquareTo;
    BigInteger.prototype.divRemTo = bnpDivRemTo;
    BigInteger.prototype.invDigit = bnpInvDigit;
    BigInteger.prototype.isEven = bnpIsEven;
    BigInteger.prototype.exp = bnpExp;
    BigInteger.prototype.toString = bnToString;
    BigInteger.prototype.negate = bnNegate;
    BigInteger.prototype.abs = bnAbs;
    BigInteger.prototype.compareTo = bnCompareTo;
    BigInteger.prototype.bitLength = bnBitLength;
    BigInteger.prototype.mod = bnMod;
    BigInteger.prototype.modPowInt = bnModPowInt;
    BigInteger.ZERO = nbv(0);
    BigInteger.ONE = nbv(1);

    function Arcfour() {
        this.i = 0;
        this.j = 0;
        this.S = new Array()
    }
    function ARC4init(a) {
        var i, j, t;
        for (i = 0; i < 256; ++i) this.S[i] = i;
        j = 0;
        for (i = 0; i < 256; ++i) {
            j = (j + this.S[i] + a[i % a.length]) & 255;
            t = this.S[i];
            this.S[i] = this.S[j];
            this.S[j] = t
        }
        this.i = 0;
        this.j = 0
    }
    function ARC4next() {
        var t;
        this.i = (this.i + 1) & 255;
        this.j = (this.j + this.S[this.i]) & 255;
        t = this.S[this.i];
        this.S[this.i] = this.S[this.j];
        this.S[this.j] = t;
        return this.S[(t + this.S[this.i]) & 255]
    }
    Arcfour.prototype.init = ARC4init;
    Arcfour.prototype.next = ARC4next;

    function prng_newstate() {
        return new Arcfour()
    }
    var G = 256;
    var H;
    var I;
    var J;

    function rng_seed_int(x) {
        I[J++] ^= x & 255;
        I[J++] ^= (x >> 8) & 255;
        I[J++] ^= (x >> 16) & 255;
        I[J++] ^= (x >> 24) & 255;
        if (J >= G) J -= G
    }
    function rng_seed_time() {
        rng_seed_int(new Date().getTime())
    }
    if (I == null) {
        I = new Array();
        J = 0;
        var t;
        if (navigator.appName == "Netscape" && navigator.appVersion < "5" && window.crypto && typeof (window.crypto.random) === 'function') {
            var z = window.crypto.random(32);
            for (t = 0; t < z.length; ++t) I[J++] = z.charCodeAt(t) & 255
        }
        while (J < G) {
            t = Math.floor(65536 * Math.random());
            I[J++] = t >>> 8;
            I[J++] = t & 255
        }
        J = 0;
        rng_seed_time()
    }
    function rng_get_byte() {
        if (H == null) {
            rng_seed_time();
            H = prng_newstate();
            H.init(I);
            for (J = 0; J < I.length; ++J) I[J] = 0;
            J = 0
        }
        return H.next()
    }
    function rng_get_bytes(a) {
        var i;
        for (i = 0; i < a.length; ++i) a[i] = rng_get_byte()
    }
    function SecureRandom() {}
    SecureRandom.prototype.nextBytes = rng_get_bytes;

    function parseBigInt(a, r) {
        return new BigInteger(a, r)
    }
    function linebrk(s, n) {
        var a = "";
        var i = 0;
        while (i + n < s.length) {
            a += s.substring(i, i + n) + "\n";
            i += n
        }
        return a + s.substring(i, s.length)
    }
    function byte2Hex(b) {
        if (b < 0x10) return "0" + b.toString(16);
        else return b.toString(16)
    }
    function pkcs1pad2(s, n) {
        if (n < s.length + 11) {
            alert("Message too long for RSA");
            return null
        }
        var a = new Array();
        var i = s.length - 1;
        while (i >= 0 && n > 0) {
            var c = s.charCodeAt(i--);
            if (c < 128) {
                a[--n] = c
            } else if ((c > 127) && (c < 2048)) {
                a[--n] = (c & 63) | 128;
                a[--n] = (c >> 6) | 192
            } else {
                a[--n] = (c & 63) | 128;
                a[--n] = ((c >> 6) & 63) | 128;
                a[--n] = (c >> 12) | 224
            }
        }
        a[--n] = 0;
        var b = new SecureRandom();
        var x = new Array();
        while (n > 2) {
            x[0] = 0;
            while (x[0] == 0) b.nextBytes(x);
            a[--n] = x[0]
        }
        a[--n] = 2;
        a[--n] = 0;
        return new BigInteger(a)
    }
    function RSAKey() {
        this.n = null;
        this.e = 0;
        this.d = null;
        this.p = null;
        this.q = null;
        this.dmp1 = null;
        this.dmq1 = null;
        this.coeff = null
    }
    function RSASetPublic(N, E) {
        if (N != null && E != null && N.length > 0 && E.length > 0) {
            this.n = parseBigInt(N, 16);
            this.e = parseInt(E, 16)
        } else alert("Invalid RSA public key")
    }
    function RSADoPublic(x) {
        return x.modPowInt(this.e, this.n)
    }
    function RSAEncrypt(a) {
        var m = pkcs1pad2(a, (this.n.bitLength() + 7) >> 3);
        if (m == null) return null;
        var c = this.doPublic(m);
        if (c == null) return null;
        var h = c.toString(16);
        if ((h.length & 1) == 0) return h;
        else return "0" + h
    }
    RSAKey.prototype.doPublic = RSADoPublic;
    RSAKey.prototype.setPublic = RSASetPublic;
    RSAKey.prototype.encrypt = RSAEncrypt;
    this.RSAKey = RSAKey
}).call(sinaSSOEncoder);

function usernameEncode(username)
{
    return sinaSSOEncoder.base64.encode(encodeURIComponent(username));
}

function passwordEncode(password, servertime, nonce, pubkey)
{
    var rsa = new sinaSSOEncoder.RSAKey();
    rsa.setPublic(pubkey, '10001');
    return rsa.encrypt([servertime, nonce].join("\t") + "\n" + password);
}

