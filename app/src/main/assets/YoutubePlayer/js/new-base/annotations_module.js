(function(g) {
    var window = this;
    var Bva = function(a, b) {
        var c = !1
          , d = a.subscribe("ROOT_MENU_REMOVED", function(a) {
            c || (c = !0,
            this.Ud(d),
            b.apply(void 0, arguments))
        }, a)
    }
      , Cva = function(a, b, c, d) {
        c = (0,
        g.z)(c, d || a.l);
        b = g.BF(b, "change", c, "iv-card-poll-choice-input");
        a.g.push(b)
    }
      , Dva = function(a) {
        a.g || (a.g = new g.YC,
        g.N(a, a.g));
        return a.g
    }
      , Y1 = function(a) {
        a.R("cardstatechange", g.YU(a) && g.ZU(a) ? 1 : 0)
    }
      , Eva = function(a) {
        for (var b = [], c = 0; c < arguments.length; c++) {
            var d = arguments[c];
            if (g.Aa(d))
                for (var e = 0; e < d.length; e += 8192) {
                    var f = g.$a(d, e, e + 8192);
                    f = Eva.apply(null, f);
                    for (var k = 0; k < f.length; k++)
                        b.push(f[k])
                }
            else
                b.push(d)
        }
        return b
    }
      , Fva = function(a, b, c, d) {
        if (null != a)
            for (a = a.firstChild; a; ) {
                if (b(a) && (c.push(a),
                d) || Fva(a, b, c, d))
                    return !0;
                a = a.nextSibling
            }
        return !1
    }
      , Z1 = function(a, b) {
        var c = [];
        return Fva(a, b, c, !0) ? c[0] : void 0
    }
      , Iva = function(a, b, c) {
        if (!(a.nodeName in Gva))
            if (3 == a.nodeType)
                c ? b.push(String(a.nodeValue).replace(/(\r\n|\r|\n)/g, "")) : b.push(a.nodeValue);
            else if (a.nodeName in Hva)
                b.push(Hva[a.nodeName]);
            else
                for (a = a.firstChild; a; )
                    Iva(a, b, c),
                    a = a.nextSibling
    }
      , $1 = function(a) {
        if (g.iua && null !== a && "innerText"in a)
            a = a.innerText.replace(/(\r\n|\r|\n)/g, "\n");
        else {
            var b = [];
            Iva(a, b, !0);
            a = b.join("")
        }
        a = a.replace(/ \xAD /g, " ").replace(/\xAD/g, "");
        a = a.replace(/\u200B/g, "");
        g.iua || (a = a.replace(/ +/g, " "));
        " " != a && (a = a.replace(/^\s*/, ""));
        return a
    }
      , Jva = function(a) {
        return new g.bh(a.left,a.top,a.right - a.left,a.bottom - a.top)
    }
      , a2 = function(a) {
        return "rtl" == g.uh(a, "direction")
    }
      , Nva = function(a) {
        var b = g.uh(a, "fontSize");
        var c = (c = b.match(Kva)) && c[0] || null;
        if (b && "px" == c)
            return (0,
            window.parseInt)(b, 10);
        if (g.yd) {
            if (String(c)in Lva)
                return g.Oh(a, b, "left", "pixelLeft");
            if (a.parentNode && 1 == a.parentNode.nodeType && String(c)in Mva)
                return a = a.parentNode,
                c = g.uh(a, "fontSize"),
                g.Oh(a, b == c ? "1em" : b, "left", "pixelLeft")
        }
        c = g.K("SPAN", {
            style: "visibility:hidden;position:absolute;line-height:0;padding:0;margin:0;border:0;height:1em;"
        });
        a.appendChild(c);
        b = c.offsetHeight;
        g.Kd(c);
        return b
    }
      , b2 = function(a, b) {
        this.start = a < b ? a : b;
        this.end = a < b ? b : a
    }
      , c2 = function(a, b) {
        if (/-[a-z]/.test(b))
            return null;
        if (g.fk && a.dataset) {
            if (g.kc() && !(b in a.dataset))
                return null;
            var c = a.dataset[b];
            return void 0 === c ? null : c
        }
        return a.getAttribute("data-" + g.Hb(b))
    }
      , d2 = function(a, b, c, d, e, f, k) {
        this.date = g.ua(a) ? new Date(a,b || 0,c || 1,d || 0,e || 0,f || 0,k || 0) : new Date(a && a.getTime ? a.getTime() : (0,
        g.F)())
    }
      , g2 = function(a) {
        a = g.Fa(a);
        delete e2[a];
        g.Yb(e2) && f2 && f2.stop()
    }
      , Pva = function() {
        f2 || (f2 = new g.Yt(function() {
            Ova()
        }
        ,20));
        var a = f2;
        a.isActive() || a.start()
    }
      , Ova = function() {
        var a = (0,
        g.F)();
        g.Lb(e2, function(b) {
            Qva(b, a)
        });
        g.Yb(e2) || Pva()
    }
      , Rva = function(a, b) {
        g.bf.call(this, a);
        this.coords = b.coords;
        this.x = b.coords[0];
        this.y = b.coords[1];
        this.z = b.coords[2];
        this.duration = b.duration;
        this.progress = b.progress;
        this.fps = b.C;
        this.state = b.g
    }
      , h2 = function(a, b, c, d) {
        g.ut.call(this);
        if (!g.Aa(a) || !g.Aa(b))
            throw Error("Start and end parameters must be arrays");
        if (a.length != b.length)
            throw Error("Start and end points must be the same length");
        this.l = a;
        this.D = b;
        this.duration = c;
        this.B = d;
        this.coords = [];
        this.progress = this.C = 0;
        this.A = null
    }
      , Qva = function(a, b) {
        b < a.startTime && (a.endTime = b + a.endTime - a.startTime,
        a.startTime = b);
        a.progress = (b - a.startTime) / (a.endTime - a.startTime);
        1 < a.progress && (a.progress = 1);
        a.C = 1E3 / (b - a.A);
        a.A = b;
        Sva(a, a.progress);
        1 == a.progress ? (a.g = 0,
        g2(a),
        a.wd(),
        a.xm()) : a.ob() && a.Mu()
    }
      , Sva = function(a, b) {
        g.Ca(a.B) && (b = a.B(b));
        a.coords = Array(a.l.length);
        for (var c = 0; c < a.l.length; c++)
            a.coords[c] = (a.D[c] - a.l[c]) * b + a.l[c]
    }
      , i2 = function(a, b, c, d, e) {
        h2.call(this, b, c, d, e);
        this.element = a
    }
      , j2 = function(a, b, c, d, e) {
        if (2 != b.length || 2 != c.length)
            throw Error("Start and end points must be 2D");
        i2.apply(this, arguments)
    }
      , Tva = function(a) {
        return Math.pow(a, 3)
    }
      , Uva = function(a) {
        return 3 * a * a - 2 * a * a * a
    }
      , Vva = function(a, b) {
        return (b & 8 && a2(a) ? b ^ 4 : b) & -9
    }
      , Wva = function(a, b, c, d, e, f, k) {
        a = a.clone();
        var l = Vva(b, c);
        c = g.Kh(b);
        k = k ? k.clone() : c.clone();
        a = g.XC(a, k, l, d, e, f);
        if (a.status & 496)
            return a.status;
        g.wh(b, g.gh(a.rect));
        k = g.fh(a.rect);
        g.kd(c, k) || (d = k,
        e = g.md(b),
        f = g.ud(g.od(e).g),
        !g.yd || g.rc("10") || f && g.rc("8") ? (b = b.style,
        g.rh ? b.MozBoxSizing = "border-box" : g.Ad ? b.WebkitBoxSizing = "border-box" : b.boxSizing = "border-box",
        b.width = Math.max(d.width, 0) + "px",
        b.height = Math.max(d.height, 0) + "px") : (e = b.style,
        f ? (f = g.Qh(b),
        b = g.Th(b),
        e.pixelWidth = d.width - b.left - f.left - f.right - b.right,
        e.pixelHeight = d.height - b.top - f.top - f.bottom - b.bottom) : (e.pixelWidth = d.width,
        e.pixelHeight = d.height)));
        return a.status
    }
      , k2 = function(a, b, c, d, e, f, k) {
        var l;
        if (l = c.offsetParent) {
            var m = "HTML" == l.tagName || "BODY" == l.tagName;
            if (!m || "static" != g.uh(l, "position")) {
                var n = g.Ch(l);
                if (!m) {
                    m = a2(l);
                    var p;
                    if (p = m) {
                        p = g.fM && g.Mn(10);
                        var r = g.fua && 0 <= g.Cb(g.Sua, 10);
                        p = g.rh || p || r
                    }
                    m = p ? -l.scrollLeft : !m || g.rH && g.rc("8") || "visible" == g.uh(l, "overflowX") ? l.scrollLeft : l.scrollWidth - l.clientWidth - l.scrollLeft;
                    n = g.jd(n, new g.hd(m,l.scrollTop))
                }
            }
        }
        l = n || new g.hd;
        n = g.Lh(a);
        (m = g.Dh(a)) && g.eh(n, Jva(m));
        m = g.od(a);
        r = g.od(c);
        m.g != r.g && (p = m.g.body,
        r = g.Fh(p, g.be(r)),
        r = g.jd(r, g.Ch(p)),
        !g.yd || g.uc(9) || g.ud(m.g) || (r = g.jd(r, g.zd(m.g))),
        n.left += r.x,
        n.top += r.y);
        a = Vva(a, b);
        b = n.left;
        a & 4 ? b += n.width : a & 2 && (b += n.width / 2);
        b = new g.hd(b,n.top + (a & 1 ? n.height : 0));
        b = g.jd(b, l);
        e && (b.x += (a & 4 ? -1 : 1) * e.x,
        b.y += (a & 1 ? -1 : 1) * e.y);
        var v;
        k && (v = g.Dh(c)) && (v.top -= l.y,
        v.right -= l.x,
        v.bottom -= l.y,
        v.left -= l.x);
        return Wva(b, c, d, f, v, k, void 0)
    }
      , l2 = function(a, b) {
        a && (a.dataset ? delete a.dataset[g.aF(b)] : a.removeAttribute("data-" + b))
    }
      , m2 = function(a) {
        var b = a.__yt_uid_key;
        b || (b = (0,
        g.bva)(),
        a.__yt_uid_key = b);
        return b
    }
      , n2 = function(a, b) {
        a = g.pd(a);
        b = g.pd(b);
        return !!g.Yd(a, function(a) {
            return a === b
        }, !0, void 0)
    }
      , Xva = function(a, b) {
        var c = g.qd(window.document, a, null, b);
        return c.length ? c[0] : null
    }
      , Yva = function() {
        g.U(window.document.body, "hide-players", !1);
        var a = g.rd("preserve-players");
        (0,
        g.B)(a, function(a) {
            g.mq(a, "preserve-players")
        })
    }
      , Zva = function(a) {
        if (window.document.createEvent) {
            var b = window.document.createEvent("HTMLEvents");
            b.initEvent("click", !0, !0);
            a.dispatchEvent(b)
        } else
            b = window.document.createEventObject(),
            a.fireEvent("onclick", b)
    }
      , $va = function(a) {
        this.g = a
    }
      , o2 = function(a) {
        var b = {};
        void 0 !== a.g.trackingParams ? b.trackingParams = a.g.trackingParams : (b.veType = a.g.veType,
        null != a.g.veCounter && (b.veCounter = a.g.veCounter),
        null != a.g.elementIndex && (b.elementIndex = a.g.elementIndex));
        void 0 !== a.g.dataElement && (b.dataElement = o2(a.g.dataElement));
        void 0 !== a.g.youtubeData && (b.youtubeData = a.g.youtubeData);
        return b
    }
      , p2 = function(a) {
        return new $va({
            trackingParams: a
        })
    }
      , awa = function() {
        var a = g.OG(0), b;
        a ? b = new $va({
            veType: a,
            youtubeData: void 0
        }) : b = null;
        return b
    }
      , bwa = function(a, b) {
        (a = g.pd(a)) && a.style && (g.O(a, b),
        g.U(a, "hid", !b))
    }
      , q2 = function(a) {
        return (a = g.pd(a)) ? g.Nh(a) && !g.kq(a, "hid") : !1
    }
      , r2 = function(a) {
        (0,
        g.B)(arguments, function(a) {
            !g.Ba(a) || a instanceof window.Element ? bwa(a, !0) : (0,
            g.B)(a, function(a) {
                r2(a)
            })
        })
    }
      , s2 = function(a) {
        (0,
        g.B)(arguments, function(a) {
            !g.Ba(a) || a instanceof window.Element ? bwa(a, !1) : (0,
            g.B)(a, function(a) {
                s2(a)
            })
        })
    }
      , t2 = function(a, b, c) {
        this.id = a;
        this.timestamp = b.timestamp || 0;
        this.type = b.card_type;
        this.teaserText = b.teaser_text;
        this.teaserDurationMs = b.teaser_duration_ms;
        this.startMs = b.start_ms;
        this.autoOpen = b.auto_open || !1;
        this.Rd = b.session_data || {};
        this.o = c;
        this.sponsored = b.sponsored || !1;
        a = b.tracking || {};
        this.l = {
            OL: a.impression,
            click: a.click,
            close: a.close,
            pW: a.teaser_impression,
            Hv: a.teaser_click
        };
        b = b.tracking_params || {};
        this.C = p2(b.card);
        this.F = p2(b.teaser);
        this.D = p2(b.icon)
    }
      , u2 = function(a, b) {
        g.Df.call(this);
        this.Fl = a;
        this.xs = b;
        this[g.gf] = !1
    }
      , y2 = function(a, b, c, d) {
        u2.call(this, a, b);
        a = this.xs;
        b = this.la();
        c ? (b.setAttribute("stroke", c.g),
        b.setAttribute("stroke-opacity", 1),
        c = c.Bb(),
        g.u(c) && -1 != c.indexOf("px") ? b.setAttribute("stroke-width", (0,
        window.parseFloat)(c) / cwa(a)) : b.setAttribute("stroke-width", c)) : b.setAttribute("stroke", "none");
        this.fill = d;
        c = this.xs;
        a = this.la();
        if (d instanceof v2)
            a.setAttribute("fill", d.H),
            a.setAttribute("fill-opacity", d.I);
        else if (d instanceof w2) {
            b = "lg-" + d.B + "-" + d.D + "-" + d.C + "-" + d.F + "-" + d.g + "-" + d.l;
            var e = b in c.l ? c.l[b] : null;
            if (!e) {
                e = x2(c, "linearGradient", {
                    x1: d.B,
                    y1: d.D,
                    x2: d.C,
                    y2: d.F,
                    gradientUnits: "userSpaceOnUse"
                });
                var f = "stop-color:" + d.g;
                g.ua(d.o) && (f += ";stop-opacity:" + d.o);
                f = x2(c, "stop", {
                    offset: "0%",
                    style: f
                });
                e.appendChild(f);
                f = "stop-color:" + d.l;
                g.ua(d.A) && (f += ";stop-opacity:" + d.A);
                d = x2(c, "stop", {
                    offset: "100%",
                    style: f
                });
                e.appendChild(d);
                e = dwa(c, b, e)
            }
            a.setAttribute("fill", "url(#" + e + ")")
        } else
            a.setAttribute("fill", "none")
    }
      , ewa = function() {}
      , fwa = function(a, b) {
        u2.call(this, a, b)
    }
      , z2 = function() {
        this.sb = [];
        this.Ka = [];
        this.Be = []
    }
      , A2 = function(a, b, c, d) {
        var e = a.ae[0] - b * Math.cos(g.gd(d)) + b * Math.cos(g.gd(d + 90))
          , f = a.ae[1] - c * Math.sin(g.gd(d)) + c * Math.sin(g.gd(d + 90));
        a.sb.push(3);
        a.Ka.push(1);
        a.Be.push(b, c, d, 90, e, f);
        a.Jm = !1;
        a.ae = [e, f]
    }
      , hwa = function(a, b) {
        for (var c = a.Be, d = 0, e = 0, f = a.sb.length; e < f; e++) {
            var k = a.sb[e]
              , l = gwa[k] * a.Ka[e];
            b(k, c.slice(d, d + l));
            d += l
        }
    }
      , iwa = function(a, b, c, d) {
        y2.call(this, a, b, c, d)
    }
      , B2 = function(a, b) {
        this.l = a;
        this.g = b
    }
      , C2 = function(a, b, c, d, e) {
        g.Tu.call(this, e);
        this.width = a;
        this.height = b;
        this.o = c || null;
        this.K = d || null
    }
      , cwa = function(a) {
        var b = a.Hj();
        return b ? b.width / (a.o ? new g.I(a.o,a.K) : a.Hj()).width : 0
    }
      , w2 = function(a, b, c, d, e, f, k, l) {
        this.B = a;
        this.D = b;
        this.C = c;
        this.F = d;
        this.g = e;
        this.l = f;
        this.o = g.t(k) ? k : null;
        this.A = g.t(l) ? l : null
    }
      , v2 = function(a, b) {
        this.H = a;
        this.I = null == b ? 1 : b
    }
      , D2 = function(a, b) {
        u2.call(this, a, b)
    }
      , jwa = function(a, b, c, d) {
        y2.call(this, a, b, c, d)
    }
      , E2 = function(a, b, c, d, e) {
        C2.call(this, a, b, c, d, e);
        this.l = {};
        this.M = g.Ad && !g.rc(526);
        this.I = new g.Mm(this)
    }
      , x2 = function(a, b, c) {
        a = a.H.g.createElementNS("http://www.w3.org/2000/svg", b);
        if (c)
            for (var d in c)
                a.setAttribute(d, c[d]);
        return a
    }
      , F2 = function(a, b, c, d) {
        b = x2(a, "path", {
            d: kwa(b)
        });
        c = new jwa(b,a,c,d);
        a.B.la().appendChild(c.la())
    }
      , kwa = function(a) {
        var b = [];
        hwa(a, function(a, d) {
            switch (a) {
            case 0:
                b.push("M");
                Array.prototype.push.apply(b, d);
                break;
            case 1:
                b.push("L");
                Array.prototype.push.apply(b, d);
                break;
            case 2:
                b.push("C");
                Array.prototype.push.apply(b, d);
                break;
            case 3:
                var c = d[3];
                b.push("A", d[0], d[1], 0, 180 < Math.abs(c) ? 1 : 0, 0 < c ? 1 : 0, d[4], d[5]);
                break;
            case 4:
                b.push("Z")
            }
        });
        return b.join(" ")
    }
      , dwa = function(a, b, c) {
        if (b in a.l)
            return a.l[b];
        var d = "_svgdef_" + lwa++;
        c.setAttribute("id", d);
        a.l[b] = d;
        a.F.appendChild(c);
        return d
    }
      , mwa = function() {
        G2 || (G2 = new g.sg(400),
        G2.start());
        return G2
    }
      , owa = function(a, b, c) {
        var d = g.yG;
        a = {
            csn: a,
            parentVisualElement: o2(b),
            visualElements: (0,
            g.G)(c, function(a) {
                return o2(a)
            })
        };
        nwa(d, {
            attachChild: a
        })
    }
      , H2 = function(a, b) {
        g.zG("visualElementShown", {
            csn: a,
            ve: o2(b),
            eventType: 1
        })
    }
      , I2 = function(a, b) {
        g.zG("visualElementGestured", {
            csn: a,
            ve: o2(b),
            gestureType: "INTERACTION_LOGGING_GESTURE_TYPE_GENERIC_CLICK"
        })
    }
      , nwa = function(a, b) {
        b.eventTimeMs = Math.round(g.iG());
        b.lactMs = g.xG();
        g.eG({
            endpoint: "log_interaction",
            payload: b
        }, a)
    }
      , J2 = function(a, b) {
        function c(a) {
            return a.baseUrl
        }
        function d(a) {
            if (a) {
                var b = a.simpleText;
                if (b)
                    return b;
                if (a.runs)
                    return (0,
                    g.G)(a.runs, function(a) {
                        return a.text
                    }).join("")
            }
        }
        function e(a) {
            if (a) {
                var b = []
                  , c = a.videoId;
                c && b.push("v=" + c);
                (c = a.playlistId) && b.push("list=" + c);
                (a = a.startTimeSeconds) && b.push("t=" + a);
                return "/watch?" + b.join("&")
            }
        }
        var f = {};
        f.startMs = (0,
        window.parseInt)(a.startMs, 10);
        f.impressionUrls = (0,
        g.G)(a.impressionUrls || [], c);
        f.skip = a.skipEndscreen;
        f.visualElement = p2(a.trackingParams);
        var k = (0,
        g.G)(a.elements || [], function(a, f) {
            var k = a.endscreenElementRenderer;
            if (!k)
                return null;
            var l = {}
              , m = k.style
              , v = k.endpoint || {};
            l.id = "element-" + f;
            l.type = m;
            l.title = d(k.title);
            l.metadata = d(k.metadata);
            l.callToAction = d(k.callToAction);
            l.Fy = k.image;
            l.left = (0,
            window.parseFloat)(k.left);
            l.width = (0,
            window.parseFloat)(k.width);
            l.top = (0,
            window.parseFloat)(k.top);
            l.aspectRatio = (0,
            window.parseFloat)(k.aspectRatio);
            l.startMs = (0,
            window.parseInt)(k.startMs, 10);
            l.endMs = (0,
            window.parseInt)(k.endMs, 10);
            l.clickUrls = (0,
            g.G)(v.loggingUrls || [], c);
            l.qn = g.Tb(k, "title", "accessibility", "accessibilityData", "label");
            l.impressionUrls = (0,
            g.G)(k.impressionUrls || [], c);
            l.YK = (0,
            g.G)(k.hovercardShowUrls || [], c);
            l.Rd = {
                itct: k.trackingParams
            };
            l.visualElement = p2(k.trackingParams);
            if ("VIDEO" == m)
                l.targetUrl = v.urlEndpoint ? v.urlEndpoint.url : e(v.watchEndpoint),
                l.Qo = !1,
                l.nq = b,
                l.videoDuration = d(k.videoDuration);
            else if ("PLAYLIST" == m)
                l.targetUrl = v.urlEndpoint ? v.urlEndpoint.url : e(v.watchEndpoint),
                l.Qo = !1,
                l.nq = b,
                l.playlistLength = d(k.playlistLength);
            else if ("CHANNEL" == m) {
                if (m = g.Tb(v, "browseEndpoint", "browseId"))
                    l.channelId = m,
                    l.targetUrl = "/channel/" + m;
                l.Qo = !1;
                l.nq = "new";
                l.isSubscribe = !!k.isSubscribe;
                if (l.isSubscribe) {
                    a: {
                        if (m = k.hovercardButton)
                            if (m = m.subscribeButtonRenderer) {
                                v = d(m.unsubscribedButtonText);
                                var D = d(m.subscribedButtonText);
                                if (m.subscribed) {
                                    var H = d(m.subscriberCountWithUnsubscribeText);
                                    var L = d(m.subscriberCountText)
                                } else
                                    H = d(m.subscriberCountText),
                                    L = d(m.subscriberCountWithSubscribeText);
                                var T = null;
                                if (k.signinEndpoint && (T = g.Tb(k, "signinEndpoint", "webNavigationEndpointData", "url"),
                                !T)) {
                                    k = void 0;
                                    break a
                                }
                                if (v && (D || T)) {
                                    k = {
                                        subscribed: m.subscribed,
                                        subscribeText: v,
                                        subscribeCount: H,
                                        unsubscribeText: D,
                                        unsubscribeCount: L,
                                        enabled: m.enabled,
                                        signinUrl: T,
                                        classic: k.useClassicSubscribeButton
                                    };
                                    break a
                                }
                            }
                        k = void 0
                    }
                    l.subscribeButton = k
                } else
                    l.subscribersText = d(k.subscribersText)
            } else
                "WEBSITE" == m && (l.targetUrl = g.Tb(v, "urlEndpoint", "url"),
                l.Qo = !0,
                l.nq = "new",
                l.iconUrl = k.icon.thumbnails[0].url);
            return l
        });
        f.elements = (0,
        g.Ld)(k, function(a) {
            return !!a
        });
        return f
    }
      , K2 = function(a) {
        g.oV.call(this, a);
        this.o = null;
        this.J = !1;
        this.B = null;
        this.l = {};
        this.F = {};
        this.D = this.A = null;
        this.K = [];
        a = g.Y(a);
        this.M = g.DM(a) || g.MM(a);
        this.UE = !0;
        this.H = 0;
        this.I = new g.cp(null);
        this.C = new g.GF(this);
        g.N(this, this.C);
        this.C.O(this.g, "crn_creatorendscreen", this.QM);
        this.C.O(this.g, "crx_creatorendscreen", this.RM);
        this.C.O(this.g, "resize", this.aB);
        this.C.O(window, "focus", this.bV);
        this.load();
        var b = g.Ed("STYLE");
        (window.document.getElementsByTagName("HEAD")[0] || window.document.body).appendChild(b);
        g.Ze(this, function() {
            g.Kd(b)
        });
        b.sheet && (b.sheet.insertRule(".ytp-ce-playlist-icon {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAASBAMAAACk4JNkAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAIVBMVEVMaXGzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7P///91E4wTAAAACXRSTlMArBbpVOtYrReN+x2FAAAAAWJLR0QKaND0VgAAACFJREFUCNdjYCAWzIQAFBaZ6hgVYLKcJnBWGEyWvYGASwCXtBf7m4i3CQAAAABJRU5ErkJggg==) no-repeat center;background-size:18px;width:18px;height:18px}", 0),
        b.sheet.insertRule(".ytp-ce-size-853 .ytp-ce-playlist-icon, .ytp-ce-size-1280 .ytp-ce-playlist-icon, .ytp-ce-size-1920 .ytp-ce-playlist-icon {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYBAMAAAASWSDLAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAJ1BMVEVMaXGzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7P///9RfzIKAAAAC3RSTlMAvDeyLvxYtDK9Ogx4T1QAAAABYktHRAyBs1FjAAAAK0lEQVQY02NgoBjshgO8HJoYwKiAMGAD92YHJM7uMCTO9gaEHs4FlPuZAQC8Fj8x/xHjxwAAAABJRU5ErkJggg==) no-repeat center;background-size:24px;width:24px;height:24px}", 0))
    }
      , pwa = function(a) {
        return a.g.getVideoData().isDni ? "current" : a.M ? "new" : "current"
    }
      , L2 = function(a) {
        return "creator-endscreen-editor" === a
    }
      , M2 = function(a, b) {
        g.fV(a.g, "creatorendscreen");
        a.A && (a.A.dispose(),
        a.A = null,
        a.D.dispose(),
        a.D = null);
        for (var c in a.l)
            a.l[c].dispose();
        a.l = {};
        a.F = {};
        0 < a.K.length && (a.K.forEach(function(a) {
            a.dispose()
        }),
        a.K.length = 0);
        a.H = 0;
        if ((a.o = b) && b.elements) {
            qwa(a);
            c = [];
            var d = new g.jQ(b.startMs,0x7ffffffffffff,{
                id: "ytp-ce-in-endscreen",
                namespace: "creatorendscreen"
            });
            c.push(d);
            g.Y(a.g).l || (a.A = new g.W({
                G: "div",
                L: "ytp-ce-shadow"
            }),
            g.hV(a.g, a.A.element, 4),
            a.D = new g.IV(a.A,200));
            for (d = 0; d < b.elements.length; ++d) {
                var e = b.elements[d]
                  , f = rwa(a, e);
                a.l[e.id] = f;
                a.F[e.id] = e;
                g.hV(a.g, f.element, 4);
                var k = new g.jQ(e.startMs,e.endMs,{
                    id: "ytp-ce-element-" + e.id,
                    namespace: "creatorendscreen"
                });
                c.push(k);
                swa(a, f, e)
            }
            g.cV(a.g, c);
            a.aB()
        }
    }
      , qwa = function(a) {
        var b = awa()
          , c = g.PG();
        c && b && owa(c, b, [a.o.visualElement])
    }
      , rwa = function(a, b) {
        var c = null;
        switch (b.type) {
        case "VIDEO":
            c = {
                G: "div",
                da: ["ytp-ce-element", "ytp-ce-video"],
                P: {
                    tabindex: "0",
                    "aria-label": b.qn || "",
                    "aria-hidden": "true"
                },
                N: [{
                    G: "div",
                    L: "ytp-ce-element-shadow"
                }, {
                    G: "div",
                    L: "ytp-ce-covering-image",
                    P: N2(b)
                }, {
                    G: "div",
                    L: "ytp-ce-covering-shadow-top"
                }, {
                    G: "a",
                    L: "ytp-ce-covering-overlay",
                    P: {
                        href: b.targetUrl,
                        tabindex: "-1"
                    },
                    N: [{
                        G: "div",
                        da: ["ytp-ce-video-title", "yt-ui-ellipsis", "yt-ui-ellipsis-2"],
                        P: {
                            dir: g.dp(a.I, b.title || "")
                        },
                        aa: b.title
                    }, {
                        G: "div",
                        L: "ytp-ce-video-duration",
                        aa: b.videoDuration
                    }]
                }]
            };
            c = new g.W(c);
            break;
        case "PLAYLIST":
            c = {
                G: "div",
                da: ["ytp-ce-element", "ytp-ce-playlist"],
                P: {
                    tabindex: "0",
                    "aria-label": b.qn || "",
                    "aria-hidden": "true"
                },
                N: [{
                    G: "div",
                    L: "ytp-ce-element-shadow"
                }, {
                    G: "div",
                    L: "ytp-ce-covering-image",
                    P: N2(b)
                }, {
                    G: "div",
                    L: "ytp-ce-covering-shadow-top"
                }, {
                    G: "a",
                    L: "ytp-ce-covering-overlay",
                    P: {
                        href: b.targetUrl,
                        tabindex: "-1"
                    },
                    N: [{
                        G: "div",
                        da: ["ytp-ce-playlist-title", "yt-ui-ellipsis", "yt-ui-ellipsis-2"],
                        P: {
                            dir: g.dp(a.I, b.title || "")
                        },
                        aa: b.title
                    }, {
                        G: "div",
                        L: "ytp-ce-playlist-count",
                        N: [{
                            G: "div",
                            L: "ytp-ce-playlist-icon"
                        }, {
                            G: "div",
                            L: "ytp-ce-playlist-count-text",
                            aa: b.playlistLength
                        }]
                    }]
                }]
            };
            c = new g.W(c);
            break;
        case "CHANNEL":
            c = {
                G: "div",
                da: ["ytp-ce-element", "ytp-ce-channel", b.isSubscribe ? "ytp-ce-channel-this" : "ytp-ce-channel-that"],
                P: {
                    tabindex: "0",
                    "aria-label": b.qn || "",
                    "aria-hidden": "true"
                },
                N: [{
                    G: "div",
                    L: "ytp-ce-element-shadow"
                }, {
                    G: "div",
                    L: "ytp-ce-expanding-overlay",
                    N: [{
                        G: "div",
                        L: "ytp-ce-expanding-overlay-hider"
                    }, {
                        G: "div",
                        L: "ytp-ce-expanding-overlay-background"
                    }, {
                        G: "div",
                        L: "ytp-ce-expanding-overlay-content",
                        N: [{
                            G: "div",
                            L: "ytp-ce-expanding-overlay-body",
                            N: [{
                                G: "div",
                                L: "ytp-ce-expanding-overlay-body-padding",
                                N: [{
                                    G: "a",
                                    da: ["ytp-ce-channel-title", "ytp-ce-link"],
                                    P: {
                                        href: b.targetUrl,
                                        target: "_blank",
                                        tabindex: "-1",
                                        dir: g.dp(a.I, b.title || "")
                                    },
                                    aa: b.title
                                }, b.subscribeButton ? {
                                    G: "div",
                                    L: "ytp-ce-subscribe-container",
                                    N: [{
                                        G: "div",
                                        L: "ytp-ce-channel-subscribe"
                                    }]
                                } : "", b.subscribersText ? {
                                    G: "div",
                                    L: "ytp-ce-channel-subscribers-text",
                                    aa: b.subscribersText
                                } : "", b.metadata ? {
                                    G: "div",
                                    da: ["ytp-ce-channel-metadata", "yt-ui-ellipsis", "yt-ui-ellipsis-3"],
                                    aa: b.metadata
                                } : ""]
                            }]
                        }]
                    }]
                }, {
                    G: "div",
                    L: "ytp-ce-expanding-image",
                    P: N2(b)
                }]
            };
            c = new g.W(c);
            var d = g.qd(window.document, "div", "ytp-ce-channel-subscribe", c.element)[0];
            if (b.subscribeButton) {
                g.S(d, "ytp-ce-subscribe-button");
                if (g.Y(a.g).l) {
                    var e = null;
                    var f = b.Rd.itct
                } else
                    e = "endscreen",
                    f = null;
                e = new g.v_(b.subscribeButton.subscribeText,b.subscribeButton.subscribeCount,b.subscribeButton.unsubscribeText,b.subscribeButton.unsubscribeCount,b.subscribeButton.enabled,b.subscribeButton.classic,b.channelId,!!b.subscribeButton.subscribed,e,f,b.subscribeButton.signinUrl,a.g);
                d.appendChild(e.element);
                a.K.push(e)
            }
            break;
        case "WEBSITE":
            c = {
                G: "div",
                da: ["ytp-ce-element", "ytp-ce-website"],
                P: {
                    tabindex: "0",
                    "aria-label": b.qn || "",
                    "aria-hidden": "true"
                },
                N: [{
                    G: "div",
                    L: "ytp-ce-element-shadow"
                }, {
                    G: "div",
                    L: "ytp-ce-expanding-overlay",
                    N: [{
                        G: "div",
                        L: "ytp-ce-expanding-overlay-hider"
                    }, {
                        G: "div",
                        L: "ytp-ce-expanding-overlay-background"
                    }, {
                        G: "div",
                        L: "ytp-ce-expanding-overlay-content",
                        N: [{
                            G: "div",
                            L: "ytp-ce-expanding-overlay-body",
                            N: [{
                                G: "div",
                                L: "ytp-ce-expanding-overlay-body-padding",
                                N: [{
                                    G: "div",
                                    L: "ytp-ce-website-title",
                                    P: {
                                        dir: g.dp(a.I, b.title || "")
                                    },
                                    aa: b.title
                                }, {
                                    G: "div",
                                    L: "ytp-ce-website-metadata",
                                    aa: b.metadata
                                }, {
                                    G: "a",
                                    da: ["ytp-ce-website-goto", "ytp-ce-link"],
                                    P: {
                                        href: b.targetUrl,
                                        target: "_blank",
                                        tabindex: "-1"
                                    },
                                    aa: b.callToAction
                                }]
                            }]
                        }]
                    }]
                }, {
                    G: "div",
                    L: "ytp-ce-expanding-image",
                    P: N2(b)
                }, {
                    G: "div",
                    L: "ytp-ce-expanding-icon",
                    P: twa(b.iconUrl)
                }]
            },
            c = new g.W(c)
        }
        b.g && g.S(c.element, "ytp-ce-placeholder");
        return c
    }
      , N2 = function(a) {
        if (a.Fy)
            var b = a.Fy.thumbnails;
        return twa(b ? b[b.length - 1].url : null)
    }
      , twa = function(a) {
        return a ? {
            style: "background-image: url(" + a + ")"
        } : {}
    }
      , swa = function(a, b, c) {
        function d() {
            k(1)
        }
        function e() {
            k(-1)
        }
        b.U("mouseenter", (0,
        g.z)(a.Yt, a, b, c));
        b.U("mouseleave", (0,
        g.z)(a.Vu, a, b, c));
        g.Y(a.g).l || b.U("click", (0,
        g.z)(function(a) {
            g.S(a.element, "ytp-ce-element-hover")
        }, a, b));
        b.U("click", (0,
        g.z)(a.OC, a, c));
        b.U("keypress", (0,
        g.z)(a.OC, a, c));
        b.U("focus", (0,
        g.z)(function(a, b) {
            this.Yt(a, b)
        }, a, b, c));
        b.U("blur", (0,
        g.z)(function(a, b) {
            this.Vu(a, b)
        }, a, b, c));
        b.U("touchstart", (0,
        g.z)(a.Yt, a, b, c));
        var f = g.J("ytp-ce-expanding-overlay-hider", b.element);
        f && b.O(f, "touchstart", function(a) {
            a = a || window.event;
            a.cancelBubble = !0;
            a.stopPropagation && a.stopPropagation();
            g.mq(b.element, "ytp-ce-element-hover");
            g.mq(b.element, "ytp-ce-force-expand")
        });
        b.U("keydown", (0,
        g.z)(function(a, b) {
            this.UE = 9 == b.keyCode && !b.shiftKey
        }, a, b));
        var k = (0,
        g.z)(function(a, b, c) {
            this.H += c;
            0 < this.H ? (g.S(a.element, "ytp-ce-force-expand"),
            O2(this, b.id, !0)) : (g.mq(a.element, "ytp-ce-force-expand"),
            g.mq(a.element, "ytp-ce-element-hover"),
            O2(this, b.id, !1))
        }, a, b, c);
        a = (0,
        g.z)(function(a, b) {
            b && (a.U("blur", function() {
                g.Nh(b) && this.UE && b.focus()
            }),
            a.O(b, "focus", d),
            a.O(b, "blur", e))
        }, a, b);
        a(g.J("ytp-sb-subscribe", b.element));
        a(g.J("ytp-sb-unsubscribe", b.element));
        b.U("focus", d);
        b.U("blur", e)
    }
      , O2 = function(a, b, c) {
        a.A && (c ? a.D.show() : a.D.hide());
        for (var d in a.l)
            d != b && g.U(a.l[d].element, "ytp-ce-element-shadow-show", c)
    }
      , P2 = function(a, b, c) {
        function d() {
            f || (e++,
            e == b.length && (k.stop(),
            c && c()))
        }
        if (!b || L2(g.Y(a.g).playerStyle))
            c && c();
        else {
            b = uwa(a, b);
            var e = 0
              , f = !1
              , k = new g.Yt(function() {
                f = !0;
                c && c()
            }
            ,1E3,a);
            k.start();
            for (a = 0; a < b.length; a++)
                g.GE(b[a], d)
        }
    }
      , Q2 = function(a, b, c) {
        P2(a, b.clickUrls, c);
        (a = g.PG()) && b.Qo && I2(a, b.visualElement)
    }
      , uwa = function(a, b) {
        var c = a.g.getVideoData().clientPlaybackNonce
          , d = a.g.getCurrentTime().toFixed(2);
        c = {
            CPN: c,
            AD_CPN: c,
            MT: d
        };
        d = [];
        for (var e = 0; e < b.length; e++)
            d.push(vwa(b[e], c));
        return d
    }
      , vwa = function(a, b) {
        return a.replace(/%5B[a-zA-Z_:]+%5D|\[[a-zA-Z_:]+\]/g, function(a) {
            var c = (0,
            window.unescape)(a);
            c = c.substring(1, c.length - 1);
            return b[c] ? (0,
            window.escape)(b[c]) : a
        })
    }
      , R2 = function(a) {
        return g.Aa(a) && a.length ? a[0] : a
    }
      , S2 = function(a) {
        var b = /.+/;
        return g.u(a) && null != b && null != a && a.match(b) ? a : ""
    }
      , T2 = function(a, b) {
        if (null == a)
            return b;
        var c = (0,
        window.parseInt)(a, 0);
        if ((0,
        window.isNaN)(c))
            return b;
        c = c.toString(16);
        return "#" + "000000".substring(0, 6 - c.length) + c
    }
      , U2 = function(a) {
        return g.u(a) ? a : ""
    }
      , V2 = function(a, b, c) {
        for (var d in b)
            if (b[d] == a)
                return a;
        return c
    }
      , W2 = function(a, b) {
        return "true" == a || "false" == a ? "true" == a : b
    }
      , X2 = function(a, b) {
        return g.u(a) ? (0,
        window.parseFloat)(a) : b
    }
      , Y2 = function(a, b, c, d, e) {
        a = (0,
        window.parseFloat)(a);
        if (null != a && !(0,
        window.isNaN)(a)) {
            if (d)
                return g.dd(a, b, c);
            if (a >= b && a <= c)
                return a
        }
        return e
    }
      , wwa = function(a) {
        if (null == a)
            return 0;
        if ("never" == a)
            return -1;
        a = a.split(":");
        if (3 < a.length)
            return 0;
        var b = 0
          , c = 1;
        (0,
        g.B)(a, function(a) {
            a = (0,
            window.parseFloat)(a);
            0 > a && (c = -c);
            b = 60 * b + Math.abs(a)
        });
        return c * b
    }
      , Z2 = function(a, b) {
        if (null == a)
            return null;
        if (g.Ba(a)) {
            var c = [];
            (0,
            g.B)(a, function(a) {
                (a = b(a)) && c.push(a)
            });
            return c
        }
        var d = b(a);
        return d ? [d] : []
    }
      , xwa = function(a) {
        function b(a) {
            return null != a && !(0,
            window.isNaN)(a)
        }
        return (a = a ? new g.Zg((0,
        window.parseFloat)(a.top),(0,
        window.parseFloat)(a.right),(0,
        window.parseFloat)(a.bottom),(0,
        window.parseFloat)(a.left)) : null) && b(a.top) && b(a.right) && b(a.bottom) && b(a.left) ? a : null
    }
      , ywa = function(a) {
        function b(a) {
            return (0,
            g.Ld)(a.split(/ +/), function(a) {
                return "" != a
            })
        }
        return null == a ? [] : b(a)
    }
      , zwa = function(a, b, c, d) {
        this.value = a;
        this.target = b;
        this.showLinkIcon = c;
        this.g = d
    }
      , $2 = function(a) {
        if (!a)
            return null;
        var b = g.Mc(U2(a.value));
        b = g.Jc(b);
        if (!b)
            return null;
        var c = V2(a.target, Awa, "current");
        return null == c ? null : new zwa(b,c,W2(a.show_link_icon, !0),null != a.pause_on_navigation ? a.pause_on_navigation : !0)
    }
      , a3 = function(a) {
        return a.value ? a.value : null
    }
      , Bwa = function(a, b, c) {
        this.type = a;
        this.trigger = b;
        this.url = c
    }
      , Ewa = function(a) {
        if (!a)
            return null;
        var b = V2(a.type, Cwa)
          , c = V2(a.trigger, Dwa)
          , d = R2(a.url);
        d = $2(d ? d : null);
        R2(a.subscribeData);
        return b ? new Bwa(b,c,d) : null
    }
      , Fwa = function(a, b, c) {
        var d = (c = "xx" == c || "xy" == c) ? 640 : 360;
        return (d + ((c ? a.width : a.height) - d) * b) / d
    }
      , b3 = function(a, b, c) {
        var d = (c = "xy" == c || "yy" == c) ? 360 : 640;
        return (d + ((c ? a.height : a.width) - d) * b) / d
    }
      , c3 = function(a, b, c, d, e, f, k, l, m, n, p) {
        this.x = a;
        this.y = b;
        this.Rk = c;
        this.o = d;
        this.t = e;
        this.B = f;
        this.C = k;
        this.D = l;
        this.A = m;
        this.l = n;
        this.g = p
    }
      , Hwa = function(a, b) {
        if (!a)
            return null;
        var c = X2(a.x, 0)
          , d = X2(a.y, 0)
          , e = X2(a.w, 0)
          , f = X2(a.h, 0)
          , k = wwa(a.t)
          , l = X2(a.d, 0)
          , m = X2(a.px, 0)
          , n = X2(a.py, 0)
          , p = X2(a.scaleSlope, 1)
          , r = X2(a.scaleSlopeX, p);
        p = X2(a.scaleSlopeY, p);
        var v = V2(a.scaleDimension, Gwa, "xy");
        return b(c, d, e, f, k, l, m, n, r, p, v)
    }
      , d3 = function(a, b) {
        var c = Iwa(b, Jwa(a, new g.bh(a.x,a.y,a.Rk,a.o), b.g))
          , d = b.g
          , e = c.clone();
        d && !d.contains(c) && (c.width < d.width ? e.left = g.dd(c.left, d.left, d.left + d.width - c.width) : (e.left = d.left,
        e.width = d.width),
        c.height < d.height ? e.top = g.dd(c.top, d.top, d.top + d.height - c.height) : (e.top = d.top,
        e.height = d.height));
        return e
    }
      , Jwa = function(a, b, c) {
        var d = a.C
          , e = a.D
          , f = a.g ? a.g : "xy"
          , k = Fwa(c, a.A, f);
        a = b3(c, a.l, f);
        f = 640 * b.width * k / 100;
        var l = 360 * b.height * a / 100;
        return new g.bh(0 == d ? 640 * b.left * k / 100 : 0 < d ? d : c.width + d - f,0 == e ? 360 * b.top * a / 100 : 0 < e ? e : c.height + e - l,f,l)
    }
      , Kwa = function(a) {
        return a ? Hwa(a, function(a, c, d, e, f, k, l, m, n, p, r) {
            return new c3(a,c,d,e,f,k,l,m,n,p,r)
        }) : null
    }
      , Lwa = function(a, b) {
        this.g = a;
        this.l = b || null
    }
      , Iwa = function(a, b) {
        var c = a.l ? d3(a.l, new Lwa(a.g)) : a.g;
        var d = b.clone()
          , e = c.left;
        c = c.top;
        e instanceof g.hd ? (d.left += e.x,
        d.top += e.y) : (d.left += e,
        g.ua(c) && (d.top += c));
        return d
    }
      , Mwa = function(a, b, c, d, e, f, k, l, m, n, p, r, v) {
        c3.call(this, a, b, c, d, e, l, m, n, p, r, v);
        this.F = f;
        this.H = k
    }
      , Nwa = function(a) {
        if (!a)
            return null;
        var b = X2(a.sx, 0)
          , c = X2(a.sy, 0);
        return Hwa(a, function(a, e, f, k, l, m, n, p, r, v, D) {
            return new Mwa(a,e,f,k,l,b,c,m,n,p,r,v,D)
        })
    }
      , Owa = function(a, b, c, d, e, f, k, l, m, n, p, r, v, D) {
        this.l = a;
        this.bgColor = b;
        this.borderColor = c;
        this.borderWidth = d;
        this.g = e;
        this.C = f;
        this.o = k;
        this.textAlign = l;
        this.textSize = m;
        this.A = n;
        this.padding = p;
        this.effects = r;
        this.cornerRadius = v;
        this.B = D
    }
      , Qwa = function(a) {
        if (!a)
            return null;
        var b = T2(a.fgColor, "#1A1A1A")
          , c = T2(a.bgColor, "#FFF")
          , d = T2(a.borderColor, "#000")
          , e = Y2(a.borderWidth, 0, 5, !1, 0)
          , f = Y2(a.bgAlpha, 0, 1, !1, .8)
          , k = T2(a.highlightFontColor, "#F2F2F2")
          , l = Y2(a.highlightWidth, 0, 5, !1, 3)
          , m = U2(a.textAlign)
          , n = Y2(a.textSize, 3.3, 30.1, !0, 3.6107)
          , p = U2(a.fontWeight)
          , r = xwa(a.padding)
          , v = ywa(a.effects)
          , D = Y2(a.cornerRadius, 0, 10, !0, 0);
        a = R2(a.gradient);
        if (a = a ? a : null) {
            var H = Y2(a.x1, 0, 100, !0, 0)
              , L = Y2(a.y1, 0, 100, !0, 0)
              , T = Y2(a.x2, 0, 100, !0, 100)
              , ia = Y2(a.y2, 0, 100, !0, 100)
              , db = T2(a.color1, "#FFF")
              , tb = T2(a.color2, "#000");
            a = new Pwa(H,L,T,ia,db,tb,Y2(a.opacity1, 0, 100, !0, 100),Y2(a.opacity2, 0, 100, !0, 0))
        } else
            a = null;
        return new Owa(b,c,d,e,f,k,l,m,n,p,r,v,D,a)
    }
      , Pwa = function(a, b, c, d, e, f, k, l) {
        this.o = a;
        this.B = b;
        this.A = c;
        this.C = d;
        this.g = e;
        this.l = f;
        this.D = k;
        this.F = l
    }
      , Rwa = function(a, b, c, d) {
        this.type = a;
        this.l = b;
        this.g = c;
        this.o = d
    }
      , Twa = function(a) {
        if (!a)
            return null;
        var b = V2(a.type, Swa, "rect")
          , c = Z2(a.rectRegion, Kwa)
          , d = Z2(a.anchoredRegion, Nwa);
        a = Z2(a.shapelessRegion, Kwa);
        return new Rwa(b,c,d,a)
    }
      , Uwa = function(a) {
        return a.l && a.l.length ? a.l[0] : a.g && a.g.length ? a.g[0] : a.o && a.o.length ? a.o[0] : null
    }
      , Vwa = function(a, b) {
        this.l = a;
        this.g = b
    }
      , Wwa = function(a, b) {
        this.state = a;
        this.ref = b
    }
      , Ywa = function(a) {
        if (!a)
            return null;
        var b = V2(a.state, Xwa);
        a = S2(a.ref);
        return b ? new Wwa(b,a) : null
    }
      , Zwa = function(a, b, c, d) {
        this.g = a || [];
        this.o = b || [];
        this.A = c;
        this.l = d;
        this.value = !1
    }
      , $wa = function(a) {
        if (!a)
            return null;
        var b = Z2(a.condition, Ywa)
          , c = Z2(a.notCondition, Ywa)
          , d = W2(a.show_delay, !1);
        a = W2(a.hide_delay, !1);
        return b || c ? new Zwa(b,c,d,a) : null
    }
      , axa = function(a, b, c) {
        (0,
        g.B)(a.g, g.Ga(b, !1), c);
        (0,
        g.B)(a.o, g.Ga(b, !0), c)
    }
      , e3 = function(a, b, c, d, e, f, k, l, m, n, p, r, v, D) {
        this.id = a;
        this.author = b;
        this.type = c;
        this.style = d;
        this.A = e;
        this.segment = f;
        this.o = k || [];
        this.D = l || [];
        this.l = m;
        this.C = n;
        this.B = p;
        this.data = r;
        this.g = v;
        this.itct = D
    }
      , dxa = function(a) {
        if (!a)
            return null;
        var b = S2(a.id)
          , c = S2(a.author)
          , d = V2(a.type, bxa)
          , e = V2(a.style, f3)
          , f = U2(R2(a.TEXT))
          , k = U2(a.data);
        k = 0 != k.length ? JSON.parse(k) : {};
        var l = R2(a.segment);
        var m = l ? l : null;
        m ? (S2(m.timeRelative),
        l = S2(m.spaceRelative),
        l = (m = Z2(m.movingRegion, Twa)) ? new Vwa(l,m) : null) : l = null;
        m = Z2(a.action, Ewa);
        var n = Z2(a.trigger, $wa)
          , p = R2(a.appearance);
        (p = Qwa(p ? p : null)) || (p = Qwa({}));
        var r = V2(a.coordinate_system, cxa, "video_relative")
          , v = W2(a.closeable, !0)
          , D = U2(a.log_data);
        a = U2(a.itct);
        return b && d ? new e3(b,c,d,e,f,l,m,n,p,r,v,k,D,a) : null
    }
      , h3 = function(a) {
        return g3(a, function(a) {
            return "click" == a.trigger
        })
    }
      , g3 = function(a, b) {
        return (0,
        g.Cj)(a.o, b, void 0)
    }
      , exa = function(a, b, c) {
        (0,
        g.B)(a.o, b, c)
    }
      , fxa = function(a, b) {
        return g.Ma(a.o, b, void 0)
    }
      , gxa = function(a, b, c) {
        (0,
        g.B)(a.D, b, c)
    }
      , hxa = function(a, b) {
        (0,
        g.G)(a.D, b, void 0)
    }
      , i3 = function(a) {
        return (a = ixa(a)) ? Uwa(a) : null
    }
      , ixa = function(a) {
        a.segment ? (a = a.segment,
        a = a.g.length ? a.g[0] : null) : a = null;
        return a
    }
      , jxa = function(a, b) {
        var c = i3(a);
        return c && b ? b3(b, c.l, c.g ? c.g : "xy") : 1
    }
      , lxa = function(a, b, c) {
        this.g = {};
        this.o = !1;
        this.B = "ivTrigger:" + a;
        this.l = c;
        axa(b, function(a, b) {
            var c = kxa(b.state, b.ref);
            this.l.subscribe(c, (0,
            g.z)(this.A, this, c, a));
            this.g[c] = a
        }, this)
    }
      , kxa = function(a, b) {
        var c = "ivTriggerCondition:" + a;
        return b ? c + ":" + b : c
    }
      , j3 = function() {
        this.g = !1;
        this.B = this.l = null
    }
      , k3 = function(a, b, c) {
        a.l ? (a.l.setSize(b, c),
        a.l.clear()) : (b = new E2(b,c,void 0,void 0,void 0),
        a.l = b,
        a.l.zo(),
        a.B = g.K("DIV"),
        b = a.l.la(),
        a.B.appendChild(b));
        return a.l
    }
      , l3 = function(a, b, c) {
        var d = window.document.createElementNS("http://www.w3.org/2000/svg", a);
        b && g.Lb(b, function(a, b) {
            d.setAttribute(b, a)
        });
        for (var e = 2; e < arguments.length; e++)
            d.appendChild(arguments[e]);
        return d
    }
      , m3 = function(a, b) {
        var c = ":" + (g.Su.getInstance().g++).toString(36);
        b.setAttribute("result", c);
        a.appendChild(b);
        return c
    }
      , mxa = function(a, b) {
        var c = m3(a, l3("feGaussianBlur", {
            "in": b,
            stdDeviation: "1.8"
        }));
        c = m3(a, l3("feDiffuseLighting", {
            "in": c,
            surfaceScale: "4",
            diffuseConstant: "1"
        }, l3("feDistantLight", {
            azimuth: "270",
            elevation: "15",
            "lighting-color": "white"
        })));
        c = m3(a, l3("feComposite", {
            "in": c,
            in2: b,
            operator: "in"
        }));
        return m3(a, l3("feComposite", {
            in2: c,
            "in": b,
            operator: "arithmetic",
            k2: 1,
            k3: .5,
            k4: 0
        }))
    }
      , nxa = function(a, b) {
        var c = m3(a, l3("feOffset", {
            "in": b,
            dx: "-7",
            dy: "-7"
        }));
        c = m3(a, l3("feGaussianBlur", {
            "in": c,
            stdDeviation: "3"
        }));
        c = m3(a, l3("feColorMatrix", {
            "in": c,
            type: "matrix",
            values: "0 0 0 0 0  0 0 0 0 0  0 0 0 0 0  0 0 0 0.5 0"
        }));
        var d = m3(a, l3("feColorMatrix", {
            "in": b,
            type: "matrix",
            values: "0 0 0 0 0  0 0 0 0 0  0 0 0 0 0  0 0 0 100 0"
        }));
        d = m3(a, l3("feGaussianBlur", {
            "in": d,
            stdDeviation: "1"
        }));
        c = m3(a, l3("feComposite", {
            operator: "out",
            "in": c,
            in2: d
        }));
        return m3(a, l3("feComposite", {
            operator: "over",
            "in": b,
            in2: c
        }))
    }
      , oxa = function(a, b) {
        return b
    }
      , pxa = function(a) {
        var b = l3("filter", {
            filterUnits: "userSpaceOnUse"
        })
          , c = "SourceGraphic";
        (0,
        g.B)(a, function(a) {
            a: {
                switch (a) {
                case "bevel":
                    a = mxa;
                    break a;
                case "dropshadow":
                    a = nxa;
                    break a
                }
                a = oxa
            }
            c = a(b, c)
        });
        return b
    }
      , qxa = function(a) {
        a = (0,
        g.Ld)(a, function(a) {
            return a in n3
        });
        g.ib(a, function(a, c) {
            return n3[a] - n3[c]
        });
        return a
    }
      , rxa = function(a) {
        return g.Ma(a, function(a) {
            return "dropshadow" == a
        }) ? new g.Zg(0,7,7,0) : new g.Zg(0,0,0,0)
    }
      , sxa = function(a, b, c) {
        var d = new z2;
        d.moveTo(a.left + b + c, a.top + c);
        d.Ec(a.left + a.width - b - c, a.top + c);
        A2(d, b, b, -90);
        d.Ec(a.left + a.width - c, a.top + a.height - b - c);
        A2(d, b, b, 0);
        d.Ec(a.left + b + c, a.top + a.height - c);
        A2(d, b, b, 90);
        d.Ec(a.left + c, a.top + b + c);
        A2(d, b, b, 180);
        d.close();
        return d
    }
      , txa = function(a, b, c, d) {
        var e = a.B;
        e ? a = new w2(e.o * b / 100,e.B * c / 100,e.A * b / 100,e.C * c / 100,e.g,e.l,e.D,e.F) : (b = a.g,
        a = new v2(a.bgColor,d ? Math.max(b, .9) : b));
        return a
    }
      , o3 = function(a, b) {
        var c = g.ch(a);
        c.expand(rxa(b));
        return Jva(c)
    }
      , uxa = function(a, b, c) {
        if (c.length && (b = g.qd(window.document, "g", void 0, b),
        b.length)) {
            var d = qxa(c);
            if (d) {
                c = "effects:" + (d ? d.join("|") : "");
                var e = c in a.l ? a.l[c] : null;
                e ? a = e : (d = pxa(d),
                a = 0 < d.childNodes.length ? dwa(a, c, d) : null)
            } else
                a = null;
            a && b[0].setAttribute("filter", "url(#" + a + ")")
        }
    }
      , p3 = function() {
        j3.call(this);
        this.A = 0
    }
      , vxa = function(a, b) {
        var c = a.top - b.y
          , d = b.x - a.left - a.width
          , e = b.y - a.top - a.height
          , f = a.left - b.x
          , k = Math.max(c, d, e, f);
        if (0 > k)
            return "i";
        switch (k) {
        case c:
            return "t";
        case d:
            return "r";
        case e:
            return "b";
        case f:
            return "l"
        }
        return "i"
    }
      , q3 = function(a) {
        j3.apply(this, arguments)
    }
      , r3 = function(a) {
        j3.apply(this, arguments)
    }
      , s3 = function(a) {
        p3.apply(this, arguments)
    }
      , t3 = function(a, b, c, d, e) {
        g.M.call(this);
        this.g = a;
        this.X = b;
        this.C = c;
        this.ea = d;
        this.ca = e;
        this.H = !1;
        this.W = new g.DF(this);
        g.N(this, this.W);
        this.M = this.B = this.D = this.K = this.l = null;
        this.Z = !1;
        this.Y = this.I = this.J = null;
        this.xp = new g.Vk(this.VO,100,this);
        g.N(this, this.xp);
        this.F = new g.Yt(this.Oz,500,this);
        g.N(this, this.F);
        this.T = this.o = this.A = null
    }
      , wxa = function(a, b) {
        var c = (0,
        g.z)(function(a, c, f) {
            c = f ? u3(this, c, (0,
            g.z)(f, this)) : u3(this, c);
            this.W.U(b, a, c)
        }, a);
        c("mouseover", "E", a.FB);
        c("mouseout", "D", a.Ih);
        c("click", "B");
        c("touchend", "B")
    }
      , xxa = function(a) {
        if (a.g.B) {
            if (g3(a.g, function(a) {
                return "close" == a.type
            }))
                var b = a.l;
            else
                a.A = g.K("DIV", "annotation-close-button"),
                g.O(a.A, !1),
                g.bF(a.A, "annotation_id", a.g.id),
                a.l.appendChild(a.A),
                b = a.A;
            var c = function(a) {
                a.stopPropagation()
            };
            a.W.U(b, "click", u3(a, "C", c));
            a.W.U(b, "touchend", u3(a, "C", c))
        }
    }
      , u3 = function(a, b, c) {
        return (0,
        g.z)(function(a) {
            if (this.ca)
                c && c(a);
            else if (a.event.target instanceof window.Element) {
                var d = a.event.target;
                g.O(d, !1);
                try {
                    var f = window.document.elementFromPoint(a.event.clientX, a.event.clientY);
                    if (g.$d(f, "annotation")) {
                        var k = window.document.createEvent("MouseEvent");
                        k.initMouseEvent(a.event.type, a.event.bubbles, a.event.cancelable, a.event.view, a.event.detail, a.event.screenX, a.event.screenY, a.event.clientX, a.event.clientY, a.event.ctrlKey, a.event.altKey, a.event.shiftKey, a.event.metaKey, a.event.button, a.event.relatedTarget);
                        f.dispatchEvent(k)
                    }
                } finally {
                    g.O(d, !0)
                }
            }
            d = g.Lh(a.target);
            a = new g.hd(a.clientX,a.clientY);
            "D" == b && d.contains(a) || this.ea.R(b, this.g)
        }, a)
    }
      , w3 = function(a) {
        if (a.l || a.o) {
            var b = i3(a.g);
            if (b) {
                var c = v3(a);
                if (a.l) {
                    b = d3(b, c);
                    var d = g.T_(g.QU(a.C));
                    g.Jh(a.l, b.width, b.height);
                    g.wh(a.l, b.left, b.top);
                    a.K = new g.bh(d.left + b.left,d.top + b.top,b.width,b.height);
                    var e = (e = i3(a.g)) && d ? Fwa(d, e.A, e.g ? e.g : "xy") : 1;
                    var f = jxa(a.g, d);
                    d = a.g.l;
                    d.padding ? d = d.padding : (d = "speech" == a.g.style ? 1.6 : .8,
                    d = new g.Zg(d,d,d,d));
                    d = new g.Zg(360 * d.top * f / 100,640 * d.right * e / 100,360 * d.bottom * f / 100,640 * d.left * e / 100);
                    a.B && (d.right += 1.5 * c.g.height / 100);
                    a.l.style.padding = d.top + "px " + d.right + "px " + d.bottom + "px " + d.left + "px";
                    "label" == a.g.style && a.D && (a.D.style.padding = a.l.style.padding);
                    d = c.g;
                    var k = !1
                      , l = 0
                      , m = 0;
                    var n = g.T_(g.QU(a.C));
                    var p = g.$U(a.C);
                    g.dh(n, p) ? n = null : (p.top += 20,
                    p.height -= 40,
                    "player_relative" != a.g.C && (p.left -= n.left,
                    p.top -= n.top),
                    n = p);
                    n && (l = n.top - (b.top + b.height),
                    m = b.top - (n.top + n.height),
                    k = 0 < l || 0 < m);
                    if (k && n) {
                        d = l;
                        e = m;
                        if (a.B) {
                            f = yxa(a, 23, b, d, e);
                            if (a.A) {
                                var r = 43 - f.width;
                                0 < r && (b.left + f.left - r > n.left && (f.left -= r),
                                f.width += r)
                            }
                            r = f;
                            g.Jh(a.B, f.width, f.height);
                            g.wh(a.B, f.left, f.top)
                        }
                        a.A && (r ? b = new g.hd(r.left + r.width - 23 - 18,a.Z ? r.top + 2 : r.top + r.height - 18 - 2) : (r = yxa(a, 18, b, d, e),
                        b = new g.hd(r.left,r.top),
                        g.Jh(a.A, r.width, r.height)),
                        g.wh(a.A, b));
                        a.M = new g.bh(a.K.left + r.left,a.K.top + r.top,r.width,r.height);
                        a.J = a.W.U(g.QU(a.C), "mousemove", a.WO, a)
                    } else
                        a.B && (r = e / f * d.height * 4.2 / 100,
                        r = new g.I(r,r),
                        "highlight" == a.g.type || "label" == a.g.style ? (e = 1.5 * d.height / 100,
                        r = new g.bh(b.width - r.width - e,b.height - r.height - e,r.width,r.height)) : r = new g.bh(b.width - r.width - 3 * d.height / 100,(b.height - r.height) / 2,r.width,r.height),
                        g.Jh(a.B, r.width, r.height),
                        g.wh(a.B, r.left, r.top)),
                        a.A && (r = 9 <= d.left + d.width - (b.left + b.width),
                        e = 9 <= b.top - d.top,
                        g.wh(a.A, r && e ? new g.hd(b.width - 9,-9) : r ? new g.hd(b.width - 9,45 < b.height ? 9 : b.height - 9) : e ? new g.hd(45 < b.width ? b.width - 9 - 18 : -9,-9) : b.width / d.width > b.height / d.height ? new g.hd(45 < b.width ? b.width - 9 - 18 : -9,b.height - 9) : new g.hd(-9,45 < b.height ? 9 : b.height - 9)))
                }
                a.o && a.o.o(a.g, c);
                if (a.l) {
                    c = a.l;
                    b = a.g.l;
                    c.style.color = "highlightText" == a.g.style ? b.C : b.l;
                    r = g.T_(g.QU(a.C));
                    c.style.fontSize = 360 * b.textSize * jxa(a.g, r) / 100 + "px";
                    r = a.g.style;
                    c.style.textAlign = b.textAlign ? b.textAlign : "title" == r || "highlightText" == r ? "center" : "left";
                    b.A && (c.style.fontWeight = b.A);
                    a = a.l;
                    c = a.style.overflow;
                    r = (b = g.J("annotation-link-icon", a)) ? g.Nh(b) : !1;
                    e = (d = g.J("annotation-close-button", a)) ? g.Nh(d) : !1;
                    r && g.O(b, !1);
                    e && g.O(d, !1);
                    m = f = "";
                    if (n = g.J("inner-text", a))
                        f = n.style.overflow,
                        m = n.style.position,
                        n.style.overflow = "visible",
                        n.style.position = "static";
                    a.style.overflow = "scroll";
                    if (a.scrollHeight > a.offsetHeight || a.scrollWidth > a.offsetWidth) {
                        l = k = Nva(a);
                        p = 5;
                        for (var v = Math.floor(k / 2); v; )
                            a.scrollHeight <= a.offsetHeight && a.scrollWidth <= a.offsetWidth ? (p = l,
                            l = Math.min(l + v, k)) : l = Math.max(l - v, p),
                            v = Math.floor(v / 2),
                            a.style.fontSize = l + "px";
                        l != p && (a.scrollHeight > a.offsetHeight || a.scrollWidth > a.offsetWidth) && (a.style.fontSize = p + "px")
                    }
                    a.style.overflow = c;
                    n && (n.style.overflow = f,
                    n.style.position = m);
                    e && g.O(d, !0);
                    r && g.O(b, !0)
                }
            }
        }
    }
      , yxa = function(a, b, c, d, e) {
        var f = 0 < e;
        b = new g.bh(0,c.height - b,Math.max(c.width, b),b);
        0 < d && (b.top = c.height,
        20 < d && (b.height += d - 20),
        g.S(a.l, "annotation-extend-down"));
        f && (b.top = -b.height,
        20 < e && (c = e - 20,
        b.top -= c,
        b.height += c),
        g.S(a.l, "annotation-extend-up"),
        a.Z = !0);
        return b
    }
      , x3 = function(a) {
        return "label" != a.g.style || a.o.g
    }
      , v3 = function(a) {
        var b = g.T_(g.QU(a.C));
        if ("player_relative" == a.g.C) {
            var c = g.QU(a.C).Pa();
            b = new g.bh(-b.left,-b.top,c.width,c.height)
        } else
            b = new g.bh(0,0,b.width,b.height);
        return new Lwa(b,a.T ? i3(a.T) : null)
    }
      , zxa = function(a, b) {
        return b ? new g.bh(a.x + b.left,a.y + b.top,b.width,b.height) : null
    }
      , y3 = function(a, b, c) {
        g.M.call(this);
        this.l = a;
        this.annotation = b;
        this.view = c;
        this.g = null;
        this.o = this.isVisible = !1;
        Axa(b, a)
    }
      , Axa = function(a, b) {
        hxa(a, function(c) {
            return new lxa(a.id,c,b)
        })
    }
      , Bxa = function(a) {
        a.l.unsubscribe("resize", a.EB, a);
        a.l.unsubscribe("onVideoAreaChange", a.tD, a)
    }
      , z3 = function(a) {
        a.g && (a.g.stop(),
        a.g = null)
    }
      , Cxa = function(a, b, c) {
        t2.call(this, a, b, c);
        this.channelId = b.channel_id;
        this.customMessage = b.custom_message;
        this.profileImageUrl = b.image_url;
        this.title = b.title;
        this.metaInfo = b.meta_info;
        this.url = $2({
            pause_on_navigation: b.pause_on_navigation,
            target: b.target || "new",
            value: b.url
        })
    }
      , A3 = function(a, b, c) {
        t2.call(this, a, b, c);
        this.imageUrl = b.image_url;
        this.displayDomain = b.display_domain;
        this.showLinkIcon = b.show_link_icon;
        this.A = b.button_icon_url;
        this.title = b.title;
        this.customMessage = b.custom_message;
        this.url = $2({
            pause_on_navigation: b.pause_on_navigation,
            target: b.target || "new",
            value: b.url
        });
        this.g = null;
        if (a = b.signin_url)
            this.g = $2({
                target: "current",
                value: a
            });
        this.J = b.signin_title || null;
        this.I = b.signin_message || null
    }
      , Dxa = function(a, b, c) {
        A3.call(this, a, b, c);
        this.H = b.ypc_flow_type;
        this.B = b.innertube_request_params
    }
      , Exa = function(a, b, c) {
        t2.call(this, a, b, c);
        this.A = b.image_url;
        this.playlistVideoCount = b.playlist_video_count;
        this.customMessage = b.custom_message;
        this.title = b.title;
        this.metaInfo = b.meta_info;
        this.url = $2({
            pause_on_navigation: b.pause_on_navigation,
            target: b.target || "new",
            value: b.url
        })
    }
      , Fxa = function(a, b, c) {
        t2.call(this, a, b, c);
        this.B = this.id.replace(/[^a-z0-9-]/gi, "-");
        this.title = b.title;
        this.choices = b.choices;
        this.g = b.hasOwnProperty("old_vote") ? b.old_vote : null;
        this.A = null;
        if (a = b.signin_url)
            this.A = $2({
                target: "current",
                value: a
            });
        this.H = b.hasOwnProperty("xsrf_token") ? b.xsrf_token : null
    }
      , Hxa = function(a, b, c) {
        A3.call(this, a, b, c);
        this.offers = [];
        a = b.offers || [];
        for (b = 0; b < a.length; b++)
            this.offers.push(new Gxa(a[b]))
    }
      , Gxa = function(a) {
        this.merchant = g.E(a.merchant);
        this.price = g.E(a.price)
    }
      , Ixa = function(a, b, c) {
        A3.call(this, a, b, c);
        this.K = b.ypc_item_type;
        this.H = b.ypc_item_id;
        this.B = b.ypc_flow_type
    }
      , Jxa = function(a, b, c) {
        t2.call(this, a, b, c);
        this.A = b.image_url;
        this.videoDuration = b.video_duration || null;
        this.customMessage = b.custom_message;
        this.title = b.title;
        this.metaInfo = b.meta_info;
        this.isLiveNow = !!b.is_live_now;
        this.url = $2({
            pause_on_navigation: b.pause_on_navigation,
            target: b.target || "new",
            value: b.url
        })
    }
      , B3 = function(a, b, c) {
        t2.call(this, a, b, c);
        this.H = b.image_url;
        this.A = b.badge_symbol || "";
        this.priceText = b.price_text || "";
        this.title = b.title;
        this.metaInfo = b.meta_info;
        this.B = !!b.is_paygated;
        this.I = !!b.user_has_entitlement;
        this.url = $2({
            target: b.target || "new",
            value: b.url
        })
    }
      , Kxa = function(a) {
        this.g = a;
        this.l = {}
    }
      , Lxa = function(a) {
        var b = {};
        b = (b["iv-event"] = 1,
        b);
        g.XU(a.g, "iv", b, void 0)
    }
      , F3 = function(a, b, c, d) {
        if (b) {
            var e = C3(a, b);
            a.l[b] = e["p-time"];
            e["iv-event"] = e.link || e["l-class"] || e["link-id"] ? 2 : 7;
            b = D3(a, "cta_annotation_shown", e, c);
            E3(a, e, Mxa(b, d))
        }
    }
      , G3 = function(a, b, c, d, e, f) {
        if (b) {
            var k = C3(a, b);
            k["iv-event"] = 3;
            k["i-time"] = a.l[b] || "";
            d && g.fc(k, d);
            b = D3(a, "cta_annotation_clicked", k, e);
            E3(a, k, b, c, f)
        }
    }
      , H3 = function(a, b) {
        if (b) {
            var c = C3(a, b);
            c["iv-event"] = 4;
            c["i-time"] = a.l[b] || "";
            var d = D3(a, "cta_annotation_closed", c);
            E3(a, c, d)
        }
    }
      , Mxa = function(a, b) {
        return b ? (0,
        g.G)(a, g.Ga(function(a, b) {
            return g.Um(b, a)
        }, b)) : a
    }
      , D3 = function(a, b, c, d) {
        d = d ? g.Ya(d) : [];
        if (30 == c["a-type"]) {
            a: {
                c = c["a-id"];
                a = a.g.getVideoData();
                if (a.Ex) {
                    if ((b = a.Ex[b]) && g.kH(b)) {
                        a = g.pb("[ANNOTATION_ID]");
                        0 <= b.indexOf("[ANNOTATION_ID]") ? b = b.replace("[ANNOTATION_ID]", c) : 0 <= b.indexOf(a) && (b = b.replace(a, c));
                        break a
                    }
                } else if (a.Mp) {
                    b = g.gE(a.Mp, {
                        label: b,
                        value: "a_id=" + c
                    });
                    break a
                }
                b = ""
            }
            b && d.push(b)
        }
        return d
    }
      , E3 = function(a, b, c, d, e) {
        var f = 1
          , k = -1;
        if (d) {
            var l = !1;
            var m = function() {
                f--;
                f || l || ((0,
                window.clearTimeout)(k),
                l = !0,
                d())
            };
            k = (0,
            window.setTimeout)(function() {
                l = !0;
                d()
            }, 1E3)
        }
        (0,
        g.B)(c || [], function(a) {
            f++;
            g.GE(a, m)
        });
        e && (f++,
        0 != e && a.g.Oi(e, m));
        g.XU(a.g, "iv", b, m)
    }
      , C3 = function(a, b) {
        var c = {};
        if (b) {
            var d = new g.Dm(b);
            (0,
            g.B)(d.Ab(), function(a) {
                c[a] = (0,
                window.escape)(d.get(a, ""))
            })
        }
        c["p-time"] = a.g.getCurrentTime().toFixed(2);
        c.ps = g.Y(a.g).playerStyle;
        return c
    }
      , I3 = function(a, b, c, d, e) {
        e = C3(a, e);
        e["iv-event"] = c;
        b && (e.ei = b);
        e["a-id"] || (e["a-id"] = "card:drawer");
        e["a-type"] = 51;
        E3(a, e, d)
    }
      , J3 = function(a, b, c, d) {
        c && (c = C3(a, c),
        c["iv-event"] = b,
        E3(a, c, d))
    }
      , Nxa = function(a, b, c, d, e, f, k) {
        this.g = a;
        this.A = b;
        this.B = c;
        this.videoData = d;
        this.logger = e;
        this.l = f;
        this.o = k
    }
      , K3 = function(a, b, c) {
        this.g = a;
        this.A = b;
        this.B = c;
        this.l = new g.cp(null)
    }
      , Pxa = function(a, b) {
        var c = [];
        (0,
        g.B)(b.choices, function(a) {
            c.push({
                G: "li",
                L: b.B + "-" + a.index.toString(),
                N: [{
                    G: "label",
                    N: [{
                        G: "input",
                        L: "iv-card-poll-choice-input",
                        P: {
                            type: "checkbox",
                            role: "radio",
                            name: b.B + a.index.toString(),
                            value: a.index.toString(),
                            "data-poll-choice-index": a.index.toString()
                        }
                    }, {
                        G: "span",
                        L: "iv-card-poll-choice-text",
                        N: [{
                            G: "span",
                            L: "iv-card-poll-choice-percent"
                        }, L3(this, "span", void 0, a.desc)]
                    }]
                }, {
                    G: "div",
                    L: "iv-card-poll-result",
                    N: [{
                        G: "div",
                        L: "iv-card-poll-result-bar"
                    }]
                }]
            })
        }, a);
        var d = {
            G: "ul",
            P: {
                role: "radiogroup"
            },
            N: c
        }
          , e = {};
        b.A && (e["aria-label"] = g.M0("$POLL_TITLE - Sign in to vote.", {
            POLL_TITLE: b.title
        }));
        var f = ["iv-card", "iv-card-poll"];
        d = [{
            G: "div",
            L: "iv-card-content",
            N: [L3(a, "h2", e, b.title), {
                G: "form",
                N: [{
                    G: "fieldset",
                    N: [d]
                }]
            }]
        }];
        b.A && (f.push("iv-card-unavailable"),
        d.push({
            G: "div",
            da: ["iv-card-sign-in"],
            N: [{
                G: "h2",
                aa: "Want to vote?"
            }, {
                G: "a",
                da: ["iv-card-sign-in-button", "iv-button"],
                P: {
                    href: a3(b.A)
                },
                N: [{
                    G: "span",
                    da: ["iv-button-content"],
                    aa: M3.lI
                }]
            }]
        }));
        f = new g.W({
            G: "div",
            da: f,
            N: d
        });
        d = f.element;
        Oxa(b, d);
        Cva(a.g.g, d, g.Ga(a.D, b), a);
        (0,
        g.B)(g.rd("iv-card-poll-choice-input", d), function(a) {
            this.g.g.U(a, "focus", g.Ga(this.o, !0));
            this.g.g.U(a, "blur", g.Ga(this.o, !1))
        }, a);
        b.A && (d = g.J("iv-card-sign-in-button", d),
        a.A(d, b.A, b.id, b.Rd, b.o, b.l.click, 5));
        return f
    }
      , P3 = function(a, b, c, d) {
        var e = b.displayDomain ? {
            G: "div",
            L: "iv-card-image-text",
            aa: b.displayDomain
        } : ""
          , f = Qxa(a, b)
          , k = ["iv-card"];
        b.g && k.push("iv-card-unavailable");
        e = [{
            G: "a",
            L: "iv-click-target",
            P: {
                href: a3(b.url)
            },
            N: [N3(b.imageUrl, e), {
                G: "div",
                L: "iv-card-content",
                N: [L3(a, "h2", void 0, b.title), f]
            }]
        }];
        b.g && e.push({
            G: "div",
            da: ["iv-card-sign-in"],
            N: [L3(a, "h2", void 0, b.J || ""), {
                G: "p",
                aa: b.I
            }, {
                G: "a",
                da: ["iv-card-sign-in-button", "iv-button"],
                P: {
                    href: a3(b.g)
                },
                N: [L3(a, "span", "iv-button-content", M3.lI)]
            }]
        });
        k = new g.W({
            G: "div",
            da: k,
            N: e
        });
        O3(a, k, b, c, d);
        return k
    }
      , Rxa = function(a, b, c) {
        var d = ["yt-badge", "standalone-ypc-badge-renderer-icon", b.I ? "standalone-ypc-badge-renderer-icon-purchased" : "standalone-ypc-badge-renderer-icon-available"]
          , e = {};
        d = b.B && b.A ? {
            G: "span",
            da: d,
            P: e,
            aa: b.A
        } : "";
        c = {
            G: "div",
            da: ["iv-card", c],
            N: [{
                G: "a",
                L: "iv-click-target",
                P: {
                    href: a3(b.url)
                },
                N: [N3(b.H, d ? {
                    G: "div",
                    L: "iv-card-image-text",
                    N: [d, b.priceText]
                } : ""), {
                    G: "div",
                    L: "iv-card-content",
                    N: [{
                        G: "h2",
                        L: "iv-card-primary-link",
                        aa: b.title
                    }, Q3(a, b)]
                }]
            }]
        };
        c = new g.W(c);
        O3(a, c, b);
        return c
    }
      , R3 = function(a, b) {
        return b.customMessage ? L3(a, "div", "iv-card-message", b.customMessage) : ""
    }
      , N3 = function(a, b) {
        var c = "background-image: url(" + a + ");"
          , d = [];
        b && d.push(b);
        return {
            G: "div",
            L: "iv-card-image",
            P: {
                style: c
            },
            N: d
        }
    }
      , Q3 = function(a, b) {
        if (!b.metaInfo || 0 == b.metaInfo.length)
            return "";
        var c = [];
        (0,
        g.B)(b.metaInfo, function(a) {
            c.push(L3(this, "li", "", a))
        }, a);
        return {
            G: "ul",
            L: "iv-card-meta-info",
            N: c
        }
    }
      , L3 = function(a, b, c, d) {
        c ? g.u(c) ? c = {
            "class": c
        } : g.Aa(c) && (c = {
            "class": c.join(" ")
        }) : c = {};
        c.dir = g.dp(a.l, d);
        return {
            G: b,
            P: c,
            aa: d
        }
    }
      , Qxa = function(a, b) {
        if (!b.customMessage)
            return "";
        var c = ["iv-card-action", "iv-card-primary-link"]
          , d = {};
        b.A && (c.push("iv-card-action-icon"),
        d.style = "background-image: url(" + b.A + ");");
        d.dir = g.dp(a.l, b.customMessage);
        var e = [{
            G: "span",
            aa: b.customMessage
        }];
        b.showLinkIcon && (e.push("\u00a0"),
        e.push({
            G: "span",
            L: "iv-card-link-icon"
        }));
        return {
            G: "div",
            da: c,
            P: d,
            N: e
        }
    }
      , Oxa = function(a, b) {
        var c = 0;
        (0,
        g.B)(a.choices, function(a) {
            c += a.count
        });
        c = c || 1;
        for (var d = 0; d < a.choices.length; d++) {
            var e = a.choices[d]
              , f = g.J(a.B + "-" + e.index.toString(), b);
            e = e.count / c;
            null == a.g && (e = 0);
            g.U(f.getElementsByTagName("label")[0], "iv-card-poll-choice-checked", a.g == d);
            f.getElementsByTagName("input")[0].checked = a.g == d;
            var k = Math.floor(100 * e).toFixed(0)
              , l = g.J("iv-card-poll-choice-percent", f);
            g.Sd(l, g.M0("$PERCENT%", {
                PERCENT: k
            }));
            f = g.J("iv-card-poll-result-bar", f);
            g.ph(f, "transform", "scaleX(" + e.toFixed(2) + ")")
        }
        g.U(b, "iv-card-poll-voted", null != a.g);
        g.U(b, "iv-card-poll-expanded", null != a.g && 1 < a.choices.length)
    }
      , Sxa = function(a, b, c, d) {
        d ? (c && a.g.videoData.za || a.g.l.kd(),
        a.g.l.isFullscreen() && g.TU(a.g.l),
        G3(a.g.logger, b.o, d, void 0, b.l.click, 5)) : b.url && a.B(b.url, b.id, b.Rd, b.o, b.l.click || [], 5)
    }
      , O3 = function(a, b, c, d, e) {
        c.g && Txa(a, g.rd("iv-card-sign-in-button", b.element), c, c.g, e);
        c.url && Txa(a, g.rd("iv-click-target", b.element), c, c.url, d)
    }
      , Txa = function(a, b, c, d, e) {
        (0,
        g.B)(b, function(a) {
            if (e) {
                var b = (0,
                g.z)(function(a) {
                    a.stopPropagation();
                    a.preventDefault();
                    e();
                    return !1
                }, this);
                this.g.g.U(a, "click", b)
            } else
                d && this.A(a, d, c.id, c.Rd, c.o, c.l.click, 5)
        }, a)
    }
      , Uxa = function(a) {
        var b = 0;
        -1 != a.indexOf("h") && (a = a.split("h"),
        b = 3600 * a[0],
        a = a[1]);
        -1 != a.indexOf("m") && (a = a.split("m"),
        b = 60 * a[0] + b,
        a = a[1]);
        -1 != a.indexOf("s") ? (a = a.split("s"),
        b = 1 * a[0] + b) : b = 1 * a + b;
        return b
    }
      , S3 = function(a) {
        var b;
        (b = a) && !(b = 1 < a.length ? "/" == a.charAt(0) && "/" != a.charAt(1) : "/" == a) && (b = Vxa(a),
        b = "com" == b[0] && "youtube" == b[1] || "be" == b[0] && "youtu" == b[1]);
        return b ? -1 == a.indexOf("/redirect?") : !1
    }
      , Vxa = function(a) {
        a = a.replace(/^(https?:)?\/\//, "");
        a = a.split("/", 1);
        return !a || 1 > a.length || !a[0] ? [] : a[0].toLowerCase().split(".").reverse()
    }
      , Wxa = function(a) {
        a = a3(a);
        if (!a)
            return null;
        a = a.replace(/https?:\/\//g, "");
        var b;
        (b = !S3(a)) || (b = g.Gg(g.Fg(a)[5] || null) || "",
        b = b.split("/"),
        b = "/" + (1 < b.length ? b[1] : ""),
        b = "/watch" != b);
        if (b)
            return null;
        b = g.eE(a);
        if (!b || !b.v)
            return null;
        if (b = b.t)
            return Uxa(b);
        a = a.split("#", 2);
        return !a || 2 > a.length ? null : (a = g.cE(a[1])) && a.t ? Uxa(a.t) : -1
    }
      , Xxa = function(a) {
        a = a3(a);
        a = a.replace(/https?:\/\//g, "");
        return S3(a) ? (a = g.eE(a)) && a.v ? a.v : null : null
    }
      , Yxa = function(a, b, c) {
        c = c.replace(/\/(u|b)\/[0-9]+/g, "");
        var d = /^[0-9]+$/;
        a && d.test(a) && (c = "/b/" + a + c);
        b && d.test(b) && (c = "/u/" + b + c);
        return c
    }
      , T3 = function(a, b) {
        return b ? b : S3(a) ? "current" : "new"
    }
      , U3 = function(a, b) {
        g.M.call(this);
        this.Ga = a;
        this.context = b;
        this.cw = !1;
        this.Y = 0
    }
      , V3 = function(a, b, c, d, e, f) {
        b = new g.jQ(b,c,{
            id: d
        });
        g.N(a, b);
        b.namespace = "annotations_module";
        e && Dva(b).subscribe("onEnter", e, a);
        f && Dva(b).subscribe("onExit", f, a);
        g.cV(a.context.l, [b])
    }
      , Zxa = function(a) {
        return (0,
        g.F)() - a.Y
    }
      , $xa = function(a, b, c, d, e, f) {
        var k = a3(b);
        if (k) {
            var l = T3(k, b.target)
              , m = (0,
            g.z)(function() {
                b.g && this.context.l.kd();
                g.rV(k || "", "current" == l ? "_top" : void 0, c)
            }, a);
            "new" == l && (m(),
            m = null);
            var n = {};
            n.interval = Zxa(a);
            G3(a.context.logger, d, m, n, e, f);
            S3(k) || (a = g.PG(),
            d = c.itct,
            a && d && I2(a, p2(d)))
        }
    }
      , Y3 = function(a, b, c) {
        U3.call(this, b, c);
        var d = this;
        this.g = a;
        this.M = null;
        this.T = this.xa = this.B = this.ua = !1;
        this.X = null;
        this.J = new g.Yt(g.y,c.B.useTabletControls ? 4E3 : 3E3);
        g.N(this, this.J);
        this.oa = new g.Yt(g.y);
        g.N(this, this.oa);
        this.C = new K3(c,(0,
        g.z)(this.aj, this),(0,
        g.z)(this.Vo, this));
        this.H = new g.W({
            G: "div",
            L: "iv-drawer",
            P: {
                id: "iv-drawer"
            },
            N: [{
                G: "div",
                L: "iv-drawer-header",
                P: {
                    "aria-role": "heading"
                },
                N: [{
                    G: "span",
                    L: "iv-drawer-header-text"
                }, {
                    G: "button",
                    da: ["iv-drawer-close-button", "ytp-button"],
                    P: {
                        "aria-label": "Hide cards",
                        tabindex: "0"
                    }
                }]
            }, {
                G: "div",
                L: "iv-drawer-content"
            }]
        });
        g.N(this, this.H);
        this.D = this.H.element;
        this.ca = new g.IV(this.H,330);
        g.N(this, this.ca);
        this.Da = g.J("iv-drawer-header-text", this.D);
        this.A = g.J("iv-drawer-content", this.D);
        this.o = [];
        this.ra = this.K = this.F = this.Z = this.l = null;
        this.ha = [];
        V3(this, 0, 1E3 * c.videoData.lengthSeconds, "", function() {
            d.xa && W3(d, "YOUTUBE_DRAWER_AUTO_OPEN")
        }, function() {
            (d.xa = d.B) && X3(d)
        });
        this.I = this.ga = this.ea = null
    }
      , fya = function(a, b) {
        var c = b && b.data && b.data.card_type;
        if (c && aya[c]) {
            c = new aya[c](b.id,b.data,b.g);
            if (!a.ua) {
                g.lq(a.la(), ["html5-stop-propagation", "iv-drawer-manager"]);
                g.hV(a.g, a.D, 5);
                bya(a);
                a.K = g.J("ytp-cards-button", a.g.getRootNode());
                var d = g.J("iv-drawer-close-button", a.D);
                a.ra = d;
                a.ua = !0
            }
            cya(a, c.id);
            var e = dya(a, c);
            if (e) {
                d = {
                    gc: c,
                    xr: e.element,
                    EA: !1
                };
                var f = eya(a, d);
                g.ab(a.o, f, 0, d);
                e.sa(a.A, f);
                a.Fn();
                c.autoOpen ? V3(a, c.startMs, 0x8000000000000, c.id, g.Ga(a.WV, d)) : (e = 1E3 * a.context.l.getCurrentTime(),
                5E3 > e && e > c.startMs && a.sE(d),
                V3(a, c.startMs, c.startMs + 1, c.id, g.Ga(a.sE, d)),
                Z3(a))
            }
        }
    }
      , gya = function(a, b) {
        b.data.autoOpenMs && V3(a, b.data.autoOpenMs, 0x8000000000000, "", function() {
            W3(a, "YOUTUBE_DRAWER_AUTO_OPEN")
        });
        b.data.autoCloseMs && V3(a, b.data.autoCloseMs, 0x8000000000000, "", function() {
            X3(a)
        });
        var c = b.data.headerText;
        g.Sd(a.Da, c);
        a.K && a.K.setAttribute("title", c);
        b.data.eventId && (a.M = b.data.eventId);
        a.ea = p2(b.data.trackingParams);
        a.I = p2(b.data.closeTrackingParams);
        a.ga = p2(b.data.iconTrackingParams)
    }
      , cya = function(a, b) {
        var c = hya(a, b);
        c && (c == a.l && (a.l = null),
        a.g.pB(c.gc.id),
        g.Kd(c.xr),
        g.Va(a.o, c),
        a.Fn(),
        Z3(a))
    }
      , W3 = function(a, b, c, d) {
        if (!a.B) {
            a.ca.show();
            a.Z = new g.Yt(function() {
                g.S(a.context.l.getRootNode(), "ytp-iv-drawer-open")
            }
            ,0);
            a.Z.start();
            a.X = g.sF(a.A, "mousewheel", (0,
            g.z)(a.lN, a));
            a.B = !0;
            a.Y = (0,
            g.F)();
            I3(a.context.logger, a.M, 7, void 0, d && d.gc ? d.gc.o : void 0);
            var e = g.PG();
            e && a.ea && a.I && (H2(e, a.ea),
            H2(e, a.I));
            var f = {
                TRIGGER_TYPE: b
            };
            (0,
            g.B)(a.o, function(b) {
                b.EA || (b.EA = !0,
                F3(a.context.logger, b.gc.o, b.gc.l.OL, f));
                e && H2(e, b.gc.C)
            });
            Y1(a.g);
            c && (a.F = new g.Yt(function() {
                a.fa = a.K;
                a.ra.focus()
            }
            ,330),
            a.F.start())
        }
    }
      , X3 = function(a) {
        a.B && (a.ca.hide(),
        g.tF(a.X),
        a.X = null,
        g.mq(a.context.l.getRootNode(), "ytp-iv-drawer-open"),
        a.B = !1,
        Y1(a.g),
        a.F && a.F.stop(),
        a.F = new g.Yt(function() {
            a.fa && (a.fa.focus(),
            a.fa = null)
        }
        ,330),
        a.F.start())
    }
      , bya = function(a) {
        var b = g.J("iv-drawer-close-button", a.D);
        a.context.g.U(b, "click", a.qJ, a);
        a.context.g.U(a.A, "touchend", function() {
            a.J.start()
        });
        a.context.g.U(a.A, "scroll", a.BJ, a);
        a.context.o.subscribe("onHideControls", function() {
            a.T = !0
        });
        a.context.o.subscribe("onShowControls", function() {
            a.T = !1
        });
        a.context.o.subscribe("onVideoAreaChange", function() {
            a.T = g.kq(a.g.getRootNode(), "ytp-autohide")
        });
        a.ha.push(g.VF("iv-teaser-shown", a.kM, a));
        a.ha.push(g.VF("iv-teaser-clicked", a.jM, a))
    }
      , dya = function(a, b) {
        switch (b.type) {
        case "simple":
            return P3(a.C, b);
        case "collaborator":
            var c = a.C
              , d = {
                G: "div",
                da: ["iv-card", "iv-card-channel"],
                N: [{
                    G: "a",
                    da: ["iv-click-target"],
                    P: {
                        href: a3(b.url),
                        "data-ytid": b.channelId
                    },
                    N: [N3(b.profileImageUrl), {
                        G: "div",
                        L: "iv-card-content",
                        N: [R3(c, b), {
                            G: "h2",
                            L: "iv-card-primary-link",
                            aa: b.title
                        }, Q3(c, b)]
                    }]
                }]
            };
            d = new g.W(d);
            O3(c, d, b);
            return d;
        case "donation":
            return c = a.C,
            d = (0,
            g.z)(c.C, c, b),
            P3(c, b, d, d);
        case "episode":
            return Rxa(a.C, b, "iv-card-episode");
        case "movie":
            return Rxa(a.C, b, "iv-card-movie");
        case "playlist":
            return c = a.C,
            d = {
                G: "div",
                da: ["iv-card", "iv-card-playlist"],
                N: [{
                    G: "a",
                    L: "iv-click-target",
                    P: {
                        href: a3(b.url)
                    },
                    N: [N3(b.A, {
                        G: "div",
                        L: "iv-card-image-overlay",
                        N: [{
                            G: "span",
                            L: "iv-card-playlist-video-count",
                            aa: b.playlistVideoCount.toString()
                        }]
                    }), {
                        G: "div",
                        L: "iv-card-content",
                        N: [R3(c, b), L3(c, "h2", "iv-card-primary-link", b.title), Q3(c, b)]
                    }]
                }]
            },
            d = new g.W(d),
            O3(c, d, b),
            d;
        case "poll":
            return Pxa(a.C, b);
        case "productListing":
            c = a.C;
            var e = !g.Ra(b.offers);
            d = ["iv-card"];
            var f = ""
              , k = Qxa(c, b);
            e && (d.push("iv-card-product-listing"),
            f = "iv-card-primary-link",
            e = b.offers[0],
            k = [],
            e.price && k.push({
                G: "div",
                L: "iv-card-offer-price",
                aa: e.price
            }),
            e.merchant && k.push({
                G: "div",
                L: "iv-card-offer-merchant",
                aa: e.merchant
            }),
            k = {
                G: "div",
                N: k
            });
            d = {
                G: "div",
                da: d,
                P: {
                    tabindex: "0"
                },
                N: [{
                    G: "a",
                    da: ["iv-card-image", "iv-click-target"],
                    P: {
                        style: "background-image: url(" + b.imageUrl + ");",
                        href: a3(b.url),
                        "aria-hidden": "true",
                        tabindex: "-1"
                    }
                }, {
                    G: "div",
                    L: "iv-card-content",
                    N: [b.sponsored ? {
                        G: "div",
                        L: "iv-card-sponsored",
                        N: ["Sponsored", {
                            G: "div",
                            L: "iv-ad-info-container",
                            N: [{
                                G: "div",
                                L: "iv-ad-info",
                                aa: "{{adInfo}}"
                            }, {
                                G: "div",
                                L: "iv-ad-info-icon-container",
                                N: [{
                                    G: "div",
                                    L: "iv-ad-info-icon"
                                }, {
                                    G: "div",
                                    L: "iv-ad-info-callout"
                                }]
                            }]
                        }]
                    } : "", {
                        G: "a",
                        L: "iv-click-target",
                        P: {
                            href: a3(b.url)
                        },
                        N: [L3(c, "h2", f, b.title), k]
                    }]
                }]
            };
            d = new g.W(d);
            f = g.Ed("span");
            g.Sd(f, "You are seeing this product because we think it is relevant to the video. Google may be compensated by the merchant.");
            d.Lb(f, "adInfo");
            O3(c, d, b);
            return d;
        case "tip":
            return c = a.C,
            d = (0,
            g.z)(c.F, c, b),
            P3(c, b, d, d);
        case "video":
            return c = a.C,
            d = b.isLiveNow ? {
                G: "span",
                da: ["yt-badge", "yt-badge-live"],
                aa: "LIVE NOW"
            } : "",
            d = {
                G: "div",
                da: ["iv-card", "iv-card-video"],
                N: [{
                    G: "a",
                    L: "iv-click-target",
                    P: {
                        href: a3(b.url)
                    },
                    N: [N3(b.A, b.videoDuration ? {
                        G: "span",
                        L: "iv-card-video-duration",
                        aa: b.videoDuration
                    } : ""), {
                        G: "div",
                        L: "iv-card-content",
                        N: [R3(c, b), L3(c, "h2", "iv-card-primary-link", b.title), Q3(c, b), d]
                    }]
                }]
            },
            d = new g.W(d),
            O3(c, d, b),
            d
        }
        return null
    }
      , eya = function(a, b) {
        if (0 == a.o.length)
            return 0;
        var c = g.Na(a.o, function(a) {
            return b.gc.startMs > a.gc.startMs || b.gc.startMs == a.gc.startMs && b.gc.timestamp >= a.gc.timestamp ? !0 : !1
        });
        return -1 == c ? 0 : c + 1
    }
      , iya = function(a) {
        return a.l ? "productListing" == a.l.gc.type : (0,
        g.em)(a.o, function(a) {
            return "productListing" == a.gc.type
        })
    }
      , Z3 = function(a) {
        g.U(a.g.getRootNode(), "ytp-cards-shopping-active", iya(a))
    }
      , jya = function(a, b) {
        if (a.H.o) {
            var c = new h2([0, a.A.scrollTop],[0, b.xr.offsetTop],600,Uva);
            a.context.A.U(c, "animate", function(b) {
                a.A.scrollTop = b.y
            });
            a.context.A.U(c, "finish", function(b) {
                a.A.scrollTop = b.y
            });
            c.play()
        } else
            g.vH(a.H, !0),
            a.A.scrollTop = b.xr.offsetTop,
            g.vH(a.H, !1)
    }
      , $3 = function(a) {
        return a.l && a.l.gc ? a.l.gc : a.o[0] && a.o[0].gc ? a.o[0].gc : null
    }
      , hya = function(a, b) {
        return g.Ma(a.o, function(a) {
            return a.gc.id == b
        })
    }
      , a4 = function(a, b, c) {
        U3.call(this, a, b);
        this.annotation = c;
        this.isActive = !1
    }
      , kya = function(a) {
        var b = a.annotation.data;
        "start_ms"in b && "end_ms"in b && V3(a, a.annotation.data.start_ms, a.annotation.data.end_ms, a.annotation.id, function() {
            a.show()
        }, function() {
            a.hide()
        })
    }
      , b4 = function(a, b, c) {
        a4.call(this, a, b, c);
        this.H = b;
        this.l = null;
        this.D = !1;
        this.o = null;
        this.A = !1;
        this.F = 0;
        this.g = this.C = this.B = null
    }
      , c4 = function(a, b, c) {
        a4.call(this, a, b, c);
        this.H = this.B = this.I = !1;
        this.F = 5E3;
        this.A = null;
        this.D = g.K("DIV", "iv-promo-contents");
        this.l = this.o = this.g = null;
        this.C = new g.Yt(function() {
            this.g.setAttribute("aria-hidden", !0);
            g.O(this.o, !1);
            g.O(this.l, !0)
        }
        ,700,this);
        g.N(this, this.C)
    }
      , lya = function(a) {
        var b = a.annotation.data;
        if ("cta" == a.annotation.style)
            var c = 6;
        else if ("video" == a.annotation.style || "playlist" == a.annotation.style)
            c = 7;
        a.F = b.collapse_delay_ms || a.F;
        var d = ["iv-promo", "iv-promo-inactive"];
        a.la().setAttribute("aria-hidden", !0);
        a.la().setAttribute("aria-label", "Promotion");
        a.la().tabIndex = 0;
        var e = a.annotation.Ta()
          , f = b.image_url;
        if (f) {
            var k = g.K("DIV", ["iv-promo-img", "iv-click-target"]);
            f = g.K("IMG", {
                src: f,
                "aria-hidden": "true"
            });
            k.appendChild(f);
            b.video_duration && !b.is_live ? (f = g.K("SPAN", "iv-promo-video-duration", b.video_duration),
            k.appendChild(f)) : b.playlist_length && (f = g.K("SPAN", "iv-promo-playlist-length", b.playlist_length.toString()),
            k.appendChild(f));
            e && a.aj(k, e, a.annotation.id, b.session_data, a.annotation.g, void 0, c)
        }
        e ? (f = g.K("A", "iv-promo-txt"),
        g.ad(f, a3(e)),
        a.g = f) : a.g = g.K("DIV", "iv-promo-txt");
        switch (a.annotation.style) {
        case "cta":
        case "website":
            var l = g.K("P", null, g.K("STRONG", null, b.text_line_1));
            var m = g.K("P", null, g.K("SPAN", "iv-promo-link", b.text_line_2));
            if (f = b.text_line_3) {
                d.push("iv-promo-website-card-cta-redesign");
                var n = g.K("BUTTON", ["iv-promo-round-expand-icon", "ytp-button"]);
                f = g.K("BUTTON", ["iv-button", "iv-promo-button"], g.K("SPAN", "iv-button-content", f));
                var p = g.K("DIV", "iv-promo-button-container");
                p.appendChild(f);
                e && a.aj(a.la(), e, a.annotation.id, b.session_data, a.annotation.g, void 0, c)
            }
            g.S(a.g, "iv-click-target");
            e && a.aj(a.g, e, a.annotation.id, b.session_data, a.annotation.g, void 0, c);
            break;
        case "playlist":
        case "video":
            l = g.K("P", null, g.K("SPAN", null, b.text_line_1));
            m = g.K("P", null, g.K("STRONG", null, b.text_line_2));
            b.is_live && (l = m,
            m = g.K("SPAN", ["yt-badge", "iv-promo-badge-live"], "LIVE NOW"));
            g.S(a.g, "iv-click-target");
            e && a.aj(a.g, e, a.annotation.id, b.session_data, a.annotation.g, void 0, c);
            d.push("iv-promo-video");
            break;
        case "vote":
            l = g.K("P", null, g.K("STRONG", null, b.text_line_1)),
            m = g.K("P", null, g.K("SPAN", null, b.text_line_2)),
            p = g.K("DIV", "iv-promo-button-container"),
            c = g.K("BUTTON", ["iv-button", "iv-promo-button"], g.K("SPAN", "iv-button-content", b.button_text)),
            a.context.g.U(c, "click", function(a) {
                a.stopPropagation();
                G3(this.context.logger, this.annotation.g, null, {
                    contest_vote: "1"
                }, (b.tracking || {}).vote);
                a = this.annotation.data;
                this.g = g.J("iv-promo-txt", this.D);
                var c = g.J("iv-promo-button-container", this.D)
                  , d = g.K("DIV", ["iv-promo-txt", "iv-click-target"])
                  , e = g.K("P", null, g.K("STRONG", null, a.text_line_3))
                  , f = g.K("P", null, a.text_line_4);
                g.Hd(d, e, f);
                g.Kd(c);
                c = this.g;
                (e = c.parentNode) && e.replaceChild(d, c);
                g.mq(this.la(), "iv-promo-with-button");
                (c = this.annotation.Ta()) && this.aj(d, c, this.annotation.id, a.session_data, this.annotation.g)
            }, a),
            p.appendChild(c),
            d.push("iv-promo-with-button")
        }
        l && a.g.appendChild(l);
        m && a.g.appendChild(m);
        a.D.appendChild(a.g);
        p && a.D.appendChild(p);
        l = g.K("DIV", "iv-promo-actions");
        a.l = g.K("BUTTON", ["iv-promo-expand", "ytp-button"]);
        a.l.title = "Expand";
        a.context.g.U(a.l, "click", g.Ga(a.Cs, 5E3), a);
        l.appendChild(a.l);
        g.O(a.l, !1);
        a.context.g.U(a.la(), "mouseover", a.rK, a);
        a.context.g.U(a.la(), "mouseout", a.qK, a);
        a.context.g.U(a.la(), "touchend", g.Ga(a.Cs, 5E3), a);
        a.o = g.K("BUTTON", ["iv-promo-close", "ytp-button"]);
        a.o.title = M3.CLOSE;
        a.context.g.U(a.o, "click", "cta" == a.annotation.style && b.text_line_3 ? a.gK : a.XO, a);
        l.appendChild(a.o);
        g.lq(a.la(), d);
        k && (g.Gd(a.la(), k),
        n && k.appendChild(n));
        g.Gd(a.la(), a.D);
        g.Gd(a.la(), l)
    }
      , mya = function(a) {
        a.B || a.H || a.A || (g.S(a.la(), "iv-promo-collapsed"),
        a.B = !0,
        a.C.start())
    }
      , nya = function(a) {
        a.C.stop();
        a.B && (g.nq(a.la(), ["iv-promo-collapsed", "iv-promo-collapsed-no-delay"]),
        a.B = !1,
        a.g && a.g.removeAttribute("aria-hidden"),
        g.O(a.l, !1),
        g.O(a.o, !0))
    }
      , oya = function(a, b) {
        a.A || (a.A = g.tg(function() {
            d4(this);
            mya(this)
        }, b, a))
    }
      , d4 = function(a) {
        a.A && (g.ug(a.A),
        a.A = null)
    }
      , e4 = function(a) {
        g.oV.call(this, a);
        this.J = !1;
        this.I = 0;
        this.o = {};
        this.K = {};
        this.Ya = new Kxa(a);
        this.F = new g.GF(this);
        g.N(this, this.F);
        this.D = this.B = null;
        this.F.O(this.g, "onVideoAreaChange", (0,
        g.z)(this.R, this, "onVideoAreaChange"));
        this.F.O(this.g, "onHideControls", (0,
        g.z)(this.R, this, "onHideControls"));
        this.F.O(this.g, "onShowControls", (0,
        g.z)(this.R, this, "onShowControls"));
        this.F.O(this.g, "resize", (0,
        g.z)(this.R, this, "resize"));
        this.F.O(this.g, "presentingplayerstatechange", (0,
        g.z)(this.R, this, "presentingplayerstatechange"));
        this.subscribe("onHideControls", this.dS, this);
        this.subscribe("onShowControls", this.lU, this);
        this.subscribe("presentingplayerstatechange", this.pT, this);
        this.subscribe("resize", this.Xu, this);
        this.subscribe("E", this.MM, this);
        this.subscribe("D", this.Ih, this);
        this.subscribe("B", this.HQ, this);
        this.subscribe("C", this.mR, this);
        g.Y(this.g).C.subscribe("vast_info_card_add", this.sD, this);
        this.X = new g.GF(this);
        g.N(this, this.X);
        this.X.O(this.g, "crn_annotations_module", this.KM);
        this.X.O(this.g, "crx_annotations_module", this.LM);
        this.T = g.K("DIV", ["video-legacy-annotations", "html5-stop-propagation"]);
        this.Z = g.K("DIV", "video-custom-annotations");
        this.A = new g.W({
            G: "div",
            da: ["ytp-player-content", "ytp-iv-player-content"]
        });
        g.N(this, this.A);
        g.hV(this.g, this.A.element, 4);
        this.A.hide();
        this.C = new g.W({
            G: "div",
            da: ["ytp-iv-video-content"]
        });
        g.N(this, this.C);
        g.Gd(this.C.element, g.K("DIV", "video-annotations", this.T, this.Z));
        this.H = this.l = null;
        this.M = [];
        pya(this) && this.load();
        var b = g.Ed("STYLE");
        (window.document.getElementsByTagName("HEAD")[0] || window.document.body).appendChild(b);
        g.Ze(this, function() {
            g.Kd(b)
        });
        if (a = b.sheet)
            a.insertRule(".iv-promo .iv-promo-contents .iv-promo-txt .iv-promo-link:after {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUBAMAAAB/pwA+AAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAHlBMVEVMaXH////////////////////////////////////Z6AnKAAAACXRSTlMA+/A2IuI1mJIldm0CAAAAAWJLR0QB/wIt3gAAAEVJREFUCNdjYGCYCQUMBJlACOIzIDElIcyZkwxgojOVWWDMSQauMKYySySUOSnBdSaUOZ0lEsac2YqwYiZ+JhwgM7E5HACgzVCI/YJ59AAAAABJRU5ErkJggg==) no-repeat center;background-size:10px;width:10px;height:10px}", 0),
            a.insertRule(".iv-promo .iv-promo-actions .iv-promo-close:after {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAkAAAAJBAMAAAASvxsjAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAJFBMVEVMaXH///////////////////////////////////////////9tKdXLAAAAC3RSTlMAVaQDpaimqQbl5rjXUFUAAAABYktHRAH/Ai3eAAAAPUlEQVQI12MQMmAwEmDwDmaOTmAw39663YCBuXp2MQMDQ+fOBgYG5ujVwQwMptvbgeLaxczVCQwiBgxmAgBkXg1FN5iwiAAAAABJRU5ErkJggg==) no-repeat center;background-size:9px;width:9px;height:9px}", 0),
            a.insertRule(".iv-promo .iv-promo-actions .iv-promo-expand:after {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAAJBAMAAADnQZCTAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAJFBMVEVMaXHMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMzMz////eMKB4AAAAC3RSTlMAOpE7k5Uvj5kpfRaQSaQAAAABYktHRAsf18TAAAAAHklEQVQI12MQYGBQZmBwTWCo0GSo6AKRQDZQRIABADXXA/UkIpvtAAAAAElFTkSuQmCC) no-repeat center;background-size:4px 9px;width:4px;height:9px}", 0),
            a.insertRule(".iv-promo-website-card-cta-redesign .iv-promo-round-expand-icon:after {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAQAAAD9CzEMAAAAAmJLR0QA/4ePzL8AAAAJcEhZcwAACxMAAAsTAQCanBgAAAAHdElNRQfgCgUUEztsNfqrAAAAXklEQVRYw+3Uuw2AQAwEUUNXfBpDIvBRMhQwJJAScNrA0r4CdiQHjjAzK4NGKucPAFmCnZcmwcTphBNO9CTGH4VB+/Zm6YlYis9fhedXz38FNvFriCCl808iw8ysrBu65gCeuV/CfgAAAABJRU5ErkJggg==) no-repeat center;background-size:18px 18px;width:18px;height:18px}", 0),
            a.insertRule(".iv-card-link-icon {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAASBAMAAACk4JNkAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAGFBMVEVMaXG7u7u7u7u7u7u7u7u7u7u7u7v///+WKTAlAAAABnRSTlMAFdQWbGj9GiOuAAAAAWJLR0QHFmGI6wAAAEhJREFUCNdjYACBNCBgQGMxMKrBWEJJaRAJRjVlKEsoSQDIAqtSZICwgEIQFkgIZBRECMxiBqsCsVjAqsCygQwwFgMeFgQgswBg2xjLrfC4mgAAAABJRU5ErkJggg==) no-repeat center;background-size:9px;width:9px;height:9px}", 0),
            a.insertRule(".iv-card-playlist-video-count:after {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYBAMAAAASWSDLAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAJFBMVEVMaXH///////////////////////////////////////////9tKdXLAAAAC3RSTlMAvDeyLvxYtDK9Ogx4T1QAAAABYktHRAH/Ai3eAAAAK0lEQVQY02NgoBjshgO8HJoYwKiAMGAD92YHJM7uMCTO9gaEHs4FlPuZAQC8Fj8x/xHjxwAAAABJRU5ErkJggg==) no-repeat center;background-size:24px;width:24px;height:24px}", 0),
            a.insertRule(".iv-drawer-close-button:after {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAwAAAAMAgMAAAArG7R0AAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAACVBMVEVMaXH////////OZTV/AAAAAnRSTlMAoKBFbtAAAAABYktHRAH/Ai3eAAAAKUlEQVQI12MIYGBlSGGQBMIUBjbHCQyM0xwYGDIZwBjEBomB5EBqgGoBolQGzYuy51cAAAAASUVORK5CYII=) no-repeat center;background-size:12px;width:12px;height:12px}", 0),
            a.insertRule(".iv-ad-info-icon {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAALCAMAAACecocUAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAVFBMVEVMaXGUlJSYmJiZmZmYmJiXl5eZmZmZmZmWlpaVlZWOjo6ZmZmSkpKXl5eYmJiYmJiZmZmZmZmZmZmZmZmYmJiJiYmXl5eZmZmYmJiWlpaZmZn///+81lbeAAAAGnRSTlMAE5DM80DliTMMEjccWIM5p1UjaTQNgB5cLlr5mgUAAAABYktHRBsCYNSkAAAAVElEQVQI102NRw7AIBADhw7ppIf/PzQLJ/ZgWSNrFlDaWKMVcs6HmGLwTqjEME6CFDrAXBYIGhNh3TJEg02wHydctvFc7sbrvnXZV8/zfs3T+7u/P7CrAso35YfPAAAAAElFTkSuQmCC) no-repeat center;background-size:11px;width:11px;height:11px}", 0),
            a.insertRule(".annotation-close-button {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAALVBMVEVMaXEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA/Pz9aWloBAQGZmZlbW1v///+X9wUzAAAACHRSTlMANprf+g6lyRmB9hUAAAABYktHRA5vvTBPAAAAWUlEQVQI12NgYBAycVZkAIKwDiBIZWBgrQAx2gMY2DrAIIFBomPWju6VHY0MGh1rbu891dHEYNGx9+yd2x3NDB4d3XfO7uhoQTDgUnDFcO1wA+FWwC2FOQMAdKg6tUSAFEAAAAAASUVORK5CYII=) no-repeat center}", 0),
            a.insertRule(".annotation-link-icon {background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACIAAAAiCAMAAAANmfvwAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAUVBMVEVMaXH////////////////////////////////////////////////////////////////////////////////////////////////////////JzkR1AAAAGnRSTlMAfXf+c3xsdGdv/GJoXPtXXflSVk5L7DBH9VeFfsQAAAABYktHRAH/Ai3eAAAAgElEQVQ4y93SSQ6AIAwFULSOOOJs739Qf9SF0VA2uNCu+psHaQJK7cVCqY+Rg92PXA++Q84KnCR03UIRJrFEKMEgZYFQhpyzQHSBWJJAdIVUENtJ3SC0mu3EdOh7zXZiBrRdzQLJ0Y1GfOlpVstD3HaZktX9X/gvRCxvxL6FR7IBS1RTM5xIpLoAAAAASUVORK5CYII=) no-repeat center}", 0)
    }
      , qya = function(a) {
        switch (a) {
        case "annotation-editor":
        case "live-dashboard":
            return !0
        }
        return !1
    }
      , pya = function(a) {
        var b = g.Y(a.g);
        a = a.g.getVideoData();
        return 1 == (b.Xg || a.Xg) && !a.Sk || null !== b.C.get(a.videoId)
    }
      , rya = function(a, b, c) {
        a.J = !0;
        a.D = g.oE(b, c)
    }
      , sya = function(a, b) {
        for (var c = {}, d = 0; d < b.attributes.length; d++) {
            var e = b.attributes[d];
            c[e.name] = e.nodeValue
        }
        for (d = 0; d < b.childNodes.length; d++)
            if (e = b.childNodes[d],
            e.tagName) {
                if (c[e.tagName])
                    var f = c[e.tagName];
                else if ("data" == e.tagName) {
                    0 < e.childNodes.length && (f = e.childNodes[0].nodeValue,
                    c[e.tagName] = "string" == typeof f ? f.trim() : f);
                    continue
                } else
                    f = [],
                    c[e.tagName] = f;
                e && "TEXT" == e.tagName ? 1 == e.childNodes.length && 3 == e.childNodes[0].nodeType ? f.push(e.childNodes[0].nodeValue) : f.push("") : e && f.push(sya(a, e))
            }
        return c
    }
      , tya = function(a, b, c) {
        return !(a.loaded && a.I == b && a.g.getVideoData().videoId == c)
    }
      , vya = function(a, b) {
        var c = uya(a, b);
        if (!c && "marker" != b.type)
            return null;
        gxa(b, function(a) {
            a = (0,
            g.z)(this.GU, this, b.id, a);
            this.subscribe("ivTrigger:" + b.id, a)
        }, a);
        var d = new y3(a.ea,b,c);
        g.N(d, c);
        return d
    }
      , wya = function(a, b) {
        var c = g.K("DIV", ["annotation", "annotation-type-custom"]);
        g.O(c, !1);
        var d = null;
        switch (b.type) {
        case "branding":
            if (g.Y(a.g).Bd)
                break;
            a.A.element.appendChild(c);
            d = new b4(c,f4(a),b);
            break;
        case "promotion":
            g.hV(a.g, c, 4),
            d = new c4(c,f4(a),b)
        }
        d && d.hq();
        return d
    }
      , zya = function(a) {
        var b = a.g.getVideoData();
        if (b.Mf) {
            var c = g.Y(a.g)
              , d = c.C.get(b.videoId);
            if (d) {
                var e = {
                    format: "XML",
                    bd: {},
                    method: "POST",
                    withCredentials: !0
                };
                "gaming" == c.playerStyle && (e.bd.gaming = "1");
                e.wd = (0,
                g.z)(function(a, b, c) {
                    tya(this, a, b) || (a = g.XD(c) && c.responseXML ? c.responseXML : null) && g4(this, a)
                }, a, a.I, b.videoId);
                g.qG() && (e.wd = xya(a, e.wd));
                e.Nb = {
                    ic_only: "1"
                };
                yya(e, d);
                a.J = !0;
                g.oE(b.Mf, e)
            }
        }
    }
      , yya = function(a, b) {
        a.method = "POST";
        a.Nb = a.Nb || {};
        b.wr && (a.Nb.ic_coll = b.wr);
        b.bi && (a.Nb.ic_xml = b.bi);
        b.Mk && (a.Nb.ic_track = b.Mk)
    }
      , Aya = function(a) {
        var b = new g.W({
            G: "div"
        });
        g.O(b.element, !1);
        var c = new Y3(a.g,b.element,f4(a));
        g.N(c, b);
        b.sa(a.A.element);
        c.hq();
        return c
    }
      , f4 = function(a) {
        if (!a.H) {
            var b = new g.DF(a);
            g.N(a, b);
            var c = new g.Mm(a);
            g.N(a, c);
            a.H = new Nxa(b,c,g.Y(a.g),a.g.getVideoData(),a.Ya,a.g,a.ea)
        }
        return a.H
    }
      , g4 = function(a, b) {
        var c = !1;
        Bya(b);
        for (var d = b.getElementsByTagName("annotation"), e = 0; e < d.length; e++) {
            var f = sya(a, d[e])
              , k = null;
            try {
                k = dxa(f)
            } catch (l) {}
            if (k) {
                a: {
                    switch (k.type) {
                    case "branding":
                    case "promotion":
                        f = !0;
                        break a
                    }
                    f = !1
                }
                if (f) {
                    if (f = wya(a, k))
                        g.N(a, f),
                        a.K[k.id] = f
                } else if ("card" == k.type || "drawer" == k.type)
                    a.l || (a.l = Aya(a),
                    g.N(a, a.l)),
                    "card" == k.type ? fya(a.l, k) : gya(a.l, k),
                    c = !0;
                else if (f = vya(a, k))
                    g.N(a, f),
                    a.o[k.id] = f
            }
        }
        c && (Y1(a.g),
        a.Xu());
        g.Lb(a.o, function(a) {
            if (a.view) {
                var b = a.annotation;
                (a = a.view) && b.segment && b.segment.l && (b = this.o[b.segment.l]) && (a.T = b.annotation)
            }
        }, a)
    }
      , Bya = function(a) {
        if ((a = a.getElementsByTagName("annotations")) && !(1 > a.length) && (a = a[0].getAttribute("itct"))) {
            var b = g.PG();
            if (b) {
                var c = awa();
                c && owa(b, c, [p2(a)])
            }
        }
    }
      , Dya = function(a, b, c, d) {
        d ? Cya(a, b, c) : h4(a, b, c)
    }
      , h4 = function(a, b, c) {
        if (b = a.o[b])
            z3(b),
            c && c.l ? (a = (0,
            g.z)(a.Mz, a, b),
            b.g = new g.Yt(a,2E3),
            b.g.start()) : a.Mz(b)
    }
      , Cya = function(a, b, c) {
        if (b = a.o[b])
            z3(b),
            c && c.A ? (a = (0,
            g.z)(a.LE, a, b),
            b.g = new g.Yt(a,2E3),
            b.g.start()) : a.LE(b)
    }
      , Eya = function(a, b) {
        if ("new" == b.target)
            return !1;
        var c = a3(b);
        if (!c)
            return !1;
        c = c.replace(/https?:\/\//g, "");
        if (!S3(c))
            return !1;
        c = g.eE(c);
        if (c.list || c.p)
            return !1;
        c = Xxa(b);
        if (!c)
            return !1;
        var d = a.g.getVideoData();
        return d.videoId == c ? !0 : g.Y(a.g).g && d.yn ? !0 : !1
    }
      , uya = function(a, b) {
        if (Fya(b)) {
            var c = b.B || g3(b, function(a) {
                return "click" == a || "rollOut" == a || "rollOut" == a
            });
            return new t3(b,(0,
            g.z)(a.T.appendChild, a.T),a.g,a.ea,c)
        }
        return null
    }
      , Fya = function(a) {
        if ("highlight" == a.type || "widget" == a.type)
            return !0;
        if ("text" == a.type)
            for (var b in f3)
                if (a.style == f3[b])
                    return !0;
        return !1
    }
      , i4 = function(a, b, c, d) {
        a.R(kxa(b, d), c, d)
    }
      , xya = function(a, b) {
        return (0,
        g.z)(function() {
            if (!this.ka()) {
                var a = Array.prototype.slice.call(arguments, 0);
                a.unshift(b);
                b = g.Ga.apply(window, a);
                this.M.push(g.oG(b))
            }
        }, a)
    }
      , j4 = function(a, b, c) {
        switch (a) {
        case "mouseover":
        case "mouseout":
            var d = 3;
            break;
        case "mouseenter":
        case "mouseleave":
            d = 9
        }
        return g.Yd(c, function(a) {
            return g.kq(a, b)
        }, !0, d)
    }
      , l4 = function(a) {
        var b = "mouseover" == a.type && "mouseenter"in k4 || "mouseout" == a.type && "mouseleave"in k4
          , c = a.type in k4 || b;
        if ("HTML" != a.target.tagName && c) {
            if (b) {
                b = "mouseover" == a.type ? "mouseenter" : "mouseleave";
                c = k4[b];
                for (var d in c.l) {
                    var e = j4(b, d, a.target);
                    e && !g.Yd(a.relatedTarget, function(a) {
                        return a == e
                    }, !0) && c.R(d, e, b, a)
                }
            }
            if (b = k4[a.type])
                for (d in b.l)
                    (e = j4(a.type, d, a.target)) && b.R(d, e, a.type, a)
        }
    }
      , m4 = function(a) {
        this.B = a;
        this.D = {};
        this.H = [];
        this.F = []
    }
      , Z = function(a, b) {
        return "yt-uix" + (a.B ? "-" + a.B : "") + (b ? "-" + b : "")
    }
      , n4 = function(a, b, c) {
        a.H.push(g.VF(b, c, a))
    }
      , o4 = function(a, b, c) {
        a.F.push(g.JN(b, c, a))
    }
      , p4 = function() {
        m4.call(this, "button");
        this.g = null;
        this.o = [];
        this.l = {}
    }
      , Gya = function(a, b, c, d, e) {
        var f = q2(c)
          , k = 9 == d.keyCode;
        k || 32 == d.keyCode || 13 == d.keyCode ? (d = q4(a, c)) ? (b = g.Od(d),
        "a" == b.tagName.toLowerCase() ? g.pV(b.href) : Zva(b)) : k && r4(a, b) : f ? 27 == d.keyCode ? (q4(a, c),
        r4(a, b)) : e(b, c, d) : (a = g.kq(b, Z(a, "reverse")) ? 38 : 40,
        d.keyCode == a && (Zva(b),
        d.preventDefault()))
    }
      , q4 = function(a, b) {
        var c = Z(a, "menu-item-highlight")
          , d = g.J(c, b);
        d && g.mq(d, c);
        return d
    }
      , Hya = function(a, b, c) {
        g.S(c, Z(a, "menu-item-highlight"));
        var d = c.getAttribute("id");
        d || (d = Z(a, "item-id-" + g.Fa(c)),
        c.setAttribute("id", d));
        b.setAttribute("aria-activedescendant", d)
    }
      , Iya = function(a, b, c, d) {
        var e = b.length;
        a = (0,
        g.Pa)(b, a);
        if (-1 == a)
            if (38 == d.keyCode)
                a = e - c;
            else {
                if (37 == d.keyCode || 38 == d.keyCode || 40 == d.keyCode)
                    a = 0
            }
        else
            39 == d.keyCode ? (a % c == c - 1 && (a -= c),
            a += 1) : 37 == d.keyCode ? (0 == a % c && (a += c),
            --a) : 38 == d.keyCode ? (a < c && (a += e),
            a -= c) : 40 == d.keyCode && (a >= e - c && (a -= e),
            a += c);
        return a
    }
      , s4 = function(a, b) {
        var c = b.iframeMask;
        c || (c = g.Ed("IFRAME"),
        c.src = 'javascript:""',
        c.className = Z(a, "menu-mask"),
        s2(c),
        b.iframeMask = c);
        return c
    }
      , t4 = function(a, b, c, d) {
        var e = g.$d(b, Z(a, "group"))
          , f = !!a.Ba(b, "button-menu-ignore-group");
        e = e && !f ? e : b;
        f = 9;
        var k = 8
          , l = g.Lh(b);
        if (g.kq(b, Z(a, "reverse"))) {
            f = 8;
            k = 9;
            l = l.top + "px";
            try {
                c.style.maxHeight = l
            } catch (p) {}
        }
        g.kq(b, "flip") && (g.kq(b, Z(a, "reverse")) ? (f = 12,
        k = 13) : (f = 13,
        k = 12));
        var m;
        a.Ba(b, "button-has-sibling-menu") ? m = g.Bh(e) : a.Ba(b, "button-menu-root-container") && (m = Jya(a, b));
        g.yd && !g.rc("8") && (m = null);
        if (m) {
            var n = g.Lh(m);
            n = new g.Zg(-n.top,n.left,n.top,-n.left)
        }
        m = new g.hd(0,1);
        g.kq(b, Z(a, "center-menu")) && (m.x -= Math.round((g.Kh(c).width - g.Kh(b).width) / 2));
        d && (m.y += g.zd(window.document).y);
        if (a = s4(a, b))
            b = g.Kh(c),
            a.style.width = b.width + "px",
            a.style.height = b.height + "px",
            k2(e, f, a, k, m, n, 197),
            d && g.ph(a, "position", "fixed");
        k2(e, f, c, k, m, n, 197)
    }
      , Jya = function(a, b) {
        if (a.Ba(b, "button-menu-root-container")) {
            var c = a.Ba(b, "button-menu-root-container");
            return g.$d(b, c)
        }
        return window.document.body
    }
      , r4 = function(a, b) {
        if (b) {
            var c = u4(a, b);
            if (c) {
                a.g = null;
                b.setAttribute("aria-pressed", "false");
                b.setAttribute("aria-expanded", "false");
                b.removeAttribute("aria-activedescendant");
                s2(c);
                a.qj(b, "button-menu-action", !1);
                var d = s4(a, b)
                  , e = m2(c).toString();
                delete a.l[e];
                g.YD(function() {
                    d && d.parentNode && (s2(d),
                    d.parentNode.removeChild(d));
                    c.originalParentNode && (c.parentNode.removeChild(c),
                    c.originalParentNode.appendChild(c),
                    c.originalParentNode = null,
                    c.activeButtonNode = null)
                }, 1)
            }
            e = g.$d(b, Z(a, "group"));
            var f = [Z(a, "active")];
            e && f.push(Z(a, "group-active"));
            g.nq(b, f);
            g.YF("yt-uix-button-menu-hide", b);
            g.tF(a.o);
            a.o.length = 0
        }
    }
      , Kya = function(a, b, c) {
        var d = Z(a, "menu-item-selected");
        a = g.rd(d, b);
        (0,
        g.B)(a, function(a) {
            g.mq(a, d)
        });
        g.S(c.parentNode, d)
    }
      , u4 = function(a, b) {
        if (!b.widgetMenu) {
            var c = a.Ba(b, "button-menu-id");
            c = c && g.pd(c);
            var d = Z(a, "menu");
            c ? g.lq(c, [d, Z(a, "menu-external")]) : c = g.J(d, b);
            b.widgetMenu = c
        }
        return b.widgetMenu
    }
      , v4 = function(a) {
        a.g && r4(a, a.g)
    }
      , w4 = function(a) {
        m4.call(this, a);
        this.o = null
    }
      , Lya = function(a, b, c) {
        var d = c || b
          , e = Z(a, "card");
        c = a.Cc(d);
        var f = g.pd(Z(a, "card") + m2(d));
        if (f)
            return a = g.J(Z(a, "card-body"), f),
            g.Qd(a, c) || (g.Kd(c),
            a.appendChild(c)),
            f;
        f = window.document.createElement("div");
        f.id = Z(a, "card") + m2(d);
        f.className = e;
        (d = a.Ba(d, "card-class")) && g.lq(f, d.split(/\s+/));
        d = window.document.createElement("div");
        d.className = Z(a, "card-border");
        b = a.Ba(b, "orientation") || "horizontal";
        e = window.document.createElement("div");
        e.className = "yt-uix-card-border-arrow yt-uix-card-border-arrow-" + b;
        var k = window.document.createElement("div");
        k.className = Z(a, "card-body");
        a = window.document.createElement("div");
        a.className = "yt-uix-card-body-arrow yt-uix-card-body-arrow-" + b;
        g.Kd(c);
        k.appendChild(c);
        d.appendChild(a);
        d.appendChild(k);
        f.appendChild(e);
        f.appendChild(d);
        window.document.body.appendChild(f);
        return f
    }
      , Mya = function(a, b, c) {
        var d = a.Ba(b, "orientation") || "horizontal";
        var e = g.J(Z(a, "anchor"), b) || b;
        var f = a.Ba(b, "position")
          , k = !!a.Ba(b, "force-position")
          , l = a.Ba(b, "position-fixed");
        d = "horizontal" == d;
        var m = "bottomright" == f || "bottomleft" == f
          , n = "topright" == f || "bottomright" == f;
        if (n && m) {
            var p = 13;
            var r = 8
        } else
            n && !m ? (p = 12,
            r = 9) : !n && m ? (p = 9,
            r = 12) : (p = 8,
            r = 13);
        var v = a2(window.document.body);
        f = a2(b);
        v != f && (p ^= 4);
        if (d) {
            f = b.offsetHeight / 2 - 12;
            var D = new g.hd(-12,b.offsetHeight + 6)
        } else
            f = b.offsetWidth / 2 - 6,
            D = new g.hd(b.offsetWidth + 6,-12);
        var H = g.Kh(c);
        f = Math.min(f, (d ? H.height : H.width) - 24 - 6);
        6 > f && (f = 6,
        d ? D.y += 12 - b.offsetHeight / 2 : D.x += 12 - b.offsetWidth / 2);
        H = null;
        k || (H = 10);
        b = Z(a, "card-flip");
        a = Z(a, "card-reverse");
        g.U(c, b, n);
        g.U(c, a, m);
        H = k2(e, p, c, r, D, null, H);
        !k && H && (H & 48 && (n = !n,
        p ^= 4,
        r ^= 4),
        H & 192 && (m = !m,
        p ^= 1,
        r ^= 1),
        g.U(c, b, n),
        g.U(c, a, m),
        k2(e, p, c, r, D));
        l && (e = (0,
        window.parseInt)(c.style.top, 10),
        k = g.zd(window.document).y,
        g.ph(c, "position", "fixed"),
        g.ph(c, "top", e - k + "px"));
        v && (c.style.right = "",
        e = g.Lh(c),
        e.left = e.left || (0,
        window.parseInt)(c.style.left, 10),
        k = g.wd(window),
        c.style.left = "",
        c.style.right = k.width - e.left - e.width + "px");
        e = g.J("yt-uix-card-body-arrow", c);
        k = g.J("yt-uix-card-border-arrow", c);
        d = d ? m ? "top" : "bottom" : !v && n || v && !n ? "left" : "right";
        e.setAttribute("style", "");
        k.setAttribute("style", "");
        e.style[d] = f + "px";
        k.style[d] = f + "px";
        m = g.J("yt-uix-card-arrow", c);
        n = g.J("yt-uix-card-arrow-background", c);
        m && n && (c = "right" == d ? g.Kh(c).width - f - 13 : f + 11,
        f = c / Math.sqrt(2),
        m.style.left = c + "px",
        m.style.marginLeft = "1px",
        n.style.marginLeft = -f + "px",
        n.style.marginTop = f + "px")
    }
      , x4 = function(a) {
        a.o && a.hide(a.o)
    }
      , Nya = function(a) {
        var b = a.cardMask;
        b || (b = g.Ed("IFRAME"),
        b.src = 'javascript:""',
        g.lq(b, ["yt-uix-card-iframe-mask"]),
        a.cardMask = b);
        b.style.position = a.style.position;
        b.style.top = a.style.top;
        b.style.left = a.offsetLeft + "px";
        b.style.height = a.clientHeight + "px";
        b.style.width = a.clientWidth + "px";
        window.document.body.appendChild(b)
    }
      , y4 = function() {
        m4.call(this, "kbd-nav")
    }
      , Oya = function(a, b, c) {
        if (b && c)
            if (g.S(c, Z(a)),
            a = b.id,
            a || (a = "kbd-nav-" + Math.floor(1E6 * Math.random() + 1),
            b.id = a),
            b = a,
            g.fk && c.dataset)
                c.dataset.kbdNavMoveOut = b;
            else {
                if (/-[a-z]/.test("kbdNavMoveOut"))
                    throw Error("");
                c.setAttribute("data-" + g.Hb("kbdNavMoveOut"), b)
            }
    }
      , Pya = function(a, b) {
        if (b) {
            var c = g.Zd(b, "LI");
            c && (g.S(c, Z(a, "highlight")),
            z4 = g.sF(b, "blur", (0,
            g.z)(function(a) {
                g.mq(a, Z(this, "highlight"));
                g.tF(z4)
            }, a, c)))
        }
    }
      , Qya = function(a) {
        if ("UL" != a.tagName.toUpperCase())
            return [];
        a = (0,
        g.Ld)(g.Md(a), function(a) {
            return "LI" == a.tagName.toUpperCase()
        });
        return (0,
        g.Ld)((0,
        g.G)(a, function(a) {
            return q2(a) ? Z1(a, function(a) {
                return g.Da(a) && 1 == a.nodeType ? g.Xd(a) : !1
            }) : !1
        }), function(a) {
            return !!a
        })
    }
      , A4 = function() {
        m4.call(this, "menu");
        this.l = this.g = null;
        this.o = {};
        this.C = {};
        this.A = null
    }
      , B4 = function(a) {
        var b = A4.getInstance();
        if (g.kq(a, Z(b)))
            return a;
        var c = b.oe(a);
        return c ? c : g.$d(a, Z(b, "content")) == b.g ? b.l : null
    }
      , Rya = function(a, b, c) {
        var d = C4(a, b);
        d && g.Jh(d, g.Kh(c));
        if (c == a.g) {
            var e = 9
              , f = 8;
            g.kq(b, Z(a, "reversed")) && (e ^= 1,
            f ^= 1);
            g.kq(b, Z(a, "flipped")) && (e ^= 4,
            f ^= 4);
            a = new g.hd(0,1);
            d && k2(b, e, d, f, a, null, 197);
            k2(b, e, c, f, a, null, 197)
        }
    }
      , Tya = function(a, b, c) {
        D4(a, b) && !c ? E4(a, b) : (Sya(a, b),
        !a.g || n2(b, a.g) ? a.oG(b) : Bva(a.A, (0,
        g.z)(a.oG, a, b)))
    }
      , Sya = function(a, b) {
        if (b) {
            var c = g.$d(b, Z(a, "content"));
            c && (c = g.rd(Z(a), c),
            (0,
            g.B)(c, function(a) {
                !n2(a, b) && D4(this, a) && F4(this, a)
            }, a))
        }
    }
      , E4 = function(a, b) {
        if (b) {
            var c = [];
            c.push(b);
            var d = G4(a, b);
            d && (d = g.rd(Z(a), d),
            d = g.Ya(d),
            c = c.concat(d),
            (0,
            g.B)(c, function(a) {
                D4(this, a) && F4(this, a)
            }, a))
        }
    }
      , F4 = function(a, b) {
        if (b) {
            var c = G4(a, b);
            g.nq(H4(a, b), [Z(a, "trigger-selected"), "yt-uix-button-toggled"]);
            g.S(c, Z(a, "content-hidden"));
            var d = G4(a, b);
            d && g.td(d, {
                "aria-expanded": "false"
            });
            (d = C4(a, b)) && d.parentNode && g.Kd(d);
            c && c == a.g && (a.l.appendChild(c),
            a.g = null,
            a.l = null,
            a.A && a.A.R("ROOT_MENU_REMOVED"));
            g.YF("yt-uix-menu-hide", b);
            c = g.Fa(b).toString();
            g.tF(a.o[c]);
            delete a.o[c]
        }
    }
      , Uya = function(a, b) {
        var c = G4(a, b);
        if (c) {
            (0,
            g.B)(c.children, function(a) {
                "LI" == a.tagName && g.td(a, {
                    role: "menuitem"
                })
            });
            g.td(c, {
                "aria-expanded": "true"
            });
            var d = c.id;
            d || (d = "aria-menu-id-" + g.Fa(c),
            c.id = d);
            (c = H4(a, b)) && g.td(c, {
                "aria-controls": d
            })
        }
    }
      , Vya = function(a, b, c) {
        var d = G4(a, b);
        d && g.kq(b, Z(a, "checked")) && (a = g.Zd(c, "LI")) && (a = g.J("yt-ui-menu-item-checked-hid", a)) && (d = g.rd("yt-ui-menu-item-checked", d),
        (0,
        g.B)(d, function(a) {
            g.oq(a, "yt-ui-menu-item-checked", "yt-ui-menu-item-checked-hid")
        }),
        g.oq(a, "yt-ui-menu-item-checked-hid", "yt-ui-menu-item-checked"))
    }
      , D4 = function(a, b) {
        var c = G4(a, b);
        return c ? !g.kq(c, Z(a, "content-hidden")) : !1
    }
      , Wya = function(a) {
        a = g.qd(window.document, "UL", null, a);
        (0,
        g.B)(a, function(a) {
            a.tabIndex = 0;
            var b = y4.getInstance();
            g.lq(a, [Z(b), Z(b, "list")])
        })
    }
      , G4 = function(a, b) {
        var c = g.cF(b, "menu-content-id");
        return c && (c = g.pd(c)) ? (g.lq(c, [Z(a, "content"), Z(a, "content-external")]),
        c) : b == a.l ? a.g : g.J(Z(a, "content"), b)
    }
      , C4 = function(a, b) {
        var c = g.Fa(b).toString()
          , d = a.C[c];
        if (!d) {
            d = g.Ed("IFRAME");
            d.src = 'javascript:""';
            var e = [Z(a, "mask")];
            (0,
            g.B)(g.jq(b), function(a) {
                e.push(a + "-mask")
            });
            g.lq(d, e);
            a.C[c] = d
        }
        return d || null
    }
      , H4 = function(a, b) {
        return g.J(Z(a, "trigger"), b)
    }
      , Xya = function(a, b) {
        return n2(b, a.g) || n2(b, a.l)
    }
      , I4 = function() {
        w4.call(this, "clickcard");
        this.g = {};
        this.l = {}
    }
      , J4 = function() {
        w4.call(this, "hovercard")
    }
      , K4 = function(a, b, c, d, e, f) {
        this.g = a;
        this.D = null;
        this.o = g.J("yt-dialog-fg", this.g) || this.g;
        if (a = g.J("yt-dialog-title", this.o)) {
            var k = "yt-dialog-title-" + g.Fa(this.o);
            a.setAttribute("id", k);
            this.o.setAttribute("aria-labelledby", k)
        }
        this.o.setAttribute("tabindex", "-1");
        this.I = g.J("yt-dialog-focus-trap", this.g);
        this.J = !1;
        this.A = new g.YC;
        this.F = [];
        this.F.push(g.BF(this.g, "click", (0,
        g.z)(this.jR, this), "yt-dialog-dismiss"));
        this.F.push(g.sF(this.I, "focus", (0,
        g.z)(this.NJ, this), !0));
        Yya(this);
        this.K = b;
        this.T = c;
        this.M = d;
        this.H = e;
        this.X = f;
        this.C = this.B = null
    }
      , Zya = function(a, b) {
        a.ka() || a.A.subscribe("post-all", b)
    }
      , Yya = function(a) {
        a = g.J("yt-dialog-fg-content", a.g);
        var b = [];
        g.Lb($ya, function(a) {
            b.push("yt-dialog-show-" + a)
        });
        g.nq(a, b);
        g.S(a, "yt-dialog-show-content")
    }
      , aza = function() {
        var a = g.rd("yt-dialog");
        return (0,
        g.Cj)(a, function(a) {
            return q2(a)
        })
    }
      , bza = function(a) {
        var b = g.qd(window.document, "iframe", null, a.g);
        (0,
        g.B)(b, function(a) {
            var b = g.cF(a, "onload");
            b && (b = g.x(b)) && g.sF(a, "load", b);
            if (b = g.cF(a, "src"))
                a.src = b
        }, a);
        return g.Ya(b)
    }
      , cza = function(a) {
        (0,
        g.B)(window.document.getElementsByTagName("iframe"), function(b) {
            -1 == (0,
            g.Pa)(a, b) && g.S(b, "iframe-hid")
        })
    }
      , dza = function() {
        var a = g.rd("iframe-hid");
        (0,
        g.B)(a, function(a) {
            g.mq(a, "iframe-hid")
        })
    }
      , eza = function(a) {
        g.YD((0,
        g.z)(function() {
            this.o && this.o.focus()
        }, a), 0)
    }
      , L4 = function() {
        m4.call(this, "overlay");
        this.A = this.l = this.o = this.g = null
    }
      , gza = function(a) {
        a.A || (a.A = g.VF("yt-uix-overlay-hide", fza));
        a.g && Zya(a.g, function() {
            var a = L4.getInstance();
            a.o = null;
            a.g.dispose();
            a.g = null
        })
    }
      , hza = function(a) {
        if (a.g) {
            var b = a.o;
            a.g.dismiss("overlayhide");
            b && a.qj(b, "overlay-hidden");
            a.o = null;
            a.l && (g.tF(a.l),
            a.l = null);
            a.g = null
        }
    }
      , iza = function(a, b) {
        var c;
        if (a)
            if (c = g.J("yt-dialog", a)) {
                var d = g.pd("body-container");
                d && (d.appendChild(c),
                a.overlayContentNode = c,
                c.overlayParentNode = a)
            } else
                c = a.overlayContentNode;
        else
            b && (c = g.$d(b, "yt-dialog"));
        return c
    }
      , jza = function() {
        var a = L4.getInstance();
        if (a.o)
            a = g.J("yt-dialog-fg-content", a.o.overlayContentNode);
        else
            a: {
                if (a = g.rd("yt-dialog-fg-content"))
                    for (var b = 0; b < a.length; b++) {
                        var c = g.$d(a[b], "yt-dialog");
                        if (q2(c)) {
                            a = a[b];
                            break a
                        }
                    }
                a = null
            }
        return a
    }
      , fza = function() {
        hza(L4.getInstance())
    }
      , M4 = function() {
        m4.call(this, "tooltip");
        this.g = 0;
        this.l = {}
    }
      , kza = function(a, b, c) {
        a.setData(b, "tooltip-text", c);
        a = a.Ba(b, "content-id");
        (a = g.pd(a)) && g.Sd(a, c)
    }
      , lza = function(a, b) {
        return a.Ba(b, "tooltip-text") || b.title
    }
      , oza = function(a, b) {
        if (b) {
            var c = lza(a, b);
            if (c) {
                var d = g.pd(N4(a, b));
                if (!d) {
                    d = window.document.createElement("div");
                    d.id = N4(a, b);
                    d.className = Z(a, "tip");
                    var e = window.document.createElement("div");
                    e.className = Z(a, "tip-body");
                    var f = window.document.createElement("div");
                    f.className = Z(a, "tip-arrow");
                    var k = window.document.createElement("div");
                    k.setAttribute("aria-hidden", "true");
                    k.className = Z(a, "tip-content");
                    var l = mza(a, b)
                      , m = N4(a, b, "content");
                    k.id = m;
                    a.setData(b, "content-id", m);
                    e.appendChild(k);
                    l && d.appendChild(l);
                    d.appendChild(e);
                    d.appendChild(f);
                    var n = $1(b);
                    m = N4(a, b, "arialabel");
                    f = window.document.createElement("div");
                    g.S(f, Z(a, "arialabel"));
                    f.id = m;
                    n = b.hasAttribute("aria-label") ? b.getAttribute("aria-label") : "rtl" == window.document.body.getAttribute("dir") ? c + " " + n : n + " " + c;
                    g.Sd(f, n);
                    b.setAttribute("aria-labelledby", m);
                    m = g.iF() || window.document.body;
                    m.appendChild(f);
                    m.appendChild(d);
                    kza(a, b, c);
                    (c = (0,
                    window.parseInt)(a.Ba(b, "tooltip-max-width"), 10)) && e.offsetWidth > c && (e.style.width = c + "px",
                    g.S(k, Z(a, "normal-wrap")));
                    k = g.kq(b, Z(a, "reverse"));
                    nza(a, b, d, e, l, k) || nza(a, b, d, e, l, !k);
                    var p = Z(a, "tip-visible");
                    g.YD(function() {
                        g.S(d, p)
                    }, 0)
                }
            }
        }
    }
      , nza = function(a, b, c, d, e, f) {
        g.U(c, Z(a, "tip-reverse"), f);
        var k = 0;
        f && (k = 1);
        var l = g.Kh(b);
        f = new g.hd((l.width - 10) / 2,f ? l.height : 0);
        var m = g.Ch(b);
        Wva(new g.hd(m.x + f.x,m.y + f.y), c, k);
        m = g.wd(window);
        var n = g.Gh(c);
        c = g.Kh(d);
        var p = Math.floor(c.width / 2);
        k = !!(m.height < n.y + l.height);
        l = !!(n.y < l.height);
        f = !!(n.x < p);
        m = !!(m.width < n.x + p);
        n = (c.width + 3) / -2 - -5;
        a = a.Ba(b, "force-tooltip-direction");
        if ("left" == a || f)
            n = -5;
        else if ("right" == a || m)
            n = 20 - c.width - 3;
        a = Math.floor(n) + "px";
        d.style.left = a;
        e && (e.style.left = a,
        e.style.height = c.height + "px",
        e.style.width = c.width + "px");
        return !(k || l)
    }
      , N4 = function(a, b, c) {
        a = Z(a) + m2(b);
        c && (a += "-" + c);
        return a
    }
      , mza = function(a, b) {
        var c = null;
        g.I0 && g.kq(b, Z(a, "masked")) && ((c = g.pd("yt-uix-tooltip-shared-mask")) ? (c.parentNode.removeChild(c),
        r2(c)) : (c = g.Ed("IFRAME"),
        c.src = 'javascript:""',
        c.id = "yt-uix-tooltip-shared-mask",
        c.className = Z(a, "tip-mask")));
        return c
    }
      , pza = function(a) {
        var b = g.pd("yt-uix-tooltip-shared-mask")
          , c = b && g.Yd(b, function(b) {
            return b == a
        }, !1, 2);
        b && c && (b.parentNode.removeChild(b),
        s2(b),
        window.document.body.appendChild(b))
    }
      , O4 = function(a) {
        g.CN.call(this, 1, arguments);
        this.g = a
    }
      , P4 = function(a, b, c, d, e) {
        g.CN.call(this, 2, arguments);
        this.source = e || null
    }
      , qza = function(a, b, c) {
        g.CN.call(this, 1, arguments);
        this.g = a;
        this.l = b
    }
      , Q4 = function(a, b, c, d, e, f, k) {
        g.CN.call(this, 1, arguments);
        this.g = d || null;
        this.source = k || null
    }
      , rza = function(a) {
        a = g.$d(a, "yt-uix-button-subscription-container");
        a = g.J("unsubscribe-confirmation-overlay-container", a);
        return g.J("yt-dialog", a)
    }
      , sza = function(a, b) {
        g.tF(R4);
        R4.length = 0;
        S4[b] || (S4[b] = rza(a));
        L4.getInstance().show(S4[b]);
        var c = jza();
        return new g.Yf(function(a) {
            R4.push(g.BF(c, "click", function() {
                a()
            }, "overlay-confirmation-unsubscribe-button"))
        }
        )
    }
      , T4 = function() {
        m4.call(this, "subscription-button")
    }
      , tza = function(a, b) {
        if (!a.Ba(b, "ypc-enabled"))
            return null;
        var c = a.Ba(b, "ypc-item-type")
          , d = a.Ba(b, "ypc-item-id");
        return {
            itemType: c,
            itemId: d,
            subscriptionElement: b
        }
    }
      , uza = function(a, b) {
        var c = a.Ba(b, U4.sI)
          , d = !!a.Ba(b, "is-subscribed");
        c = "-" + c;
        var e = V4.FG + c;
        g.U(b, V4.EG + c, !d);
        g.U(b, e, d);
        a.Ba(b, U4.SG) && !a.Ba(b, U4.QG) && (c = Z(M4.getInstance()),
        g.U(b, c, !d),
        b.title = d ? "" : a.Ba(b, U4.TG));
        d ? g.YD(function() {
            g.S(b, V4.uw)
        }, 1E3) : g.mq(b, V4.uw)
    }
      , vza = function(a, b) {
        var c = g.rd(Z(a));
        return (0,
        g.Ld)(c, function(a) {
            return b == this.Ba(a, "channel-external-id")
        }, a)
    }
      , wza = function(a, b) {
        var c = (0,
        g.z)(function(a) {
            a.discoverable_subscriptions && g.RD("SUBSCRIBE_EMBED_DISCOVERABLE_SUBSCRIPTIONS", a.discoverable_subscriptions);
            this.pw(b)
        }, a);
        g.Zsa(c, "subscribe", "sub_button")
    }
      , xza = function(a, b) {
        if (!a.Ba(b, "show-unsub-confirm-dialog"))
            return !1;
        var c = a.Ba(b, "show-unsub-confirm-time-frame");
        return "always" == c || "ten_minutes" == c && (c = (0,
        window.parseInt)(a.Ba(b, "subscribed-timestamp"), 10),
        (new d2).getTime() < 1E3 * (c + 600)) ? !0 : !1
    }
      , Gva = {
        SCRIPT: 1,
        STYLE: 1,
        HEAD: 1,
        IFRAME: 1,
        OBJECT: 1
    }
      , Hva = {
        IMG: " ",
        BR: "\n"
    }
      , Kva = /[^\d]+$/
      , Lva = {
        cm: 1,
        "in": 1,
        mm: 1,
        pc: 1,
        pt: 1
    }
      , Mva = {
        em: 1,
        ex: 1
    };
    b2.prototype.clone = function() {
        return new b2(this.start,this.end)
    }
    ;
    b2.prototype.getLength = function() {
        return this.end - this.start
    }
    ;
    g.A(d2, g.ko);
    g.h = d2.prototype;
    g.h.getHours = function() {
        return this.date.getHours()
    }
    ;
    g.h.getMinutes = function() {
        return this.date.getMinutes()
    }
    ;
    g.h.getSeconds = function() {
        return this.date.getSeconds()
    }
    ;
    g.h.getMilliseconds = function() {
        return this.date.getMilliseconds()
    }
    ;
    g.h.getUTCHours = function() {
        return this.date.getUTCHours()
    }
    ;
    g.h.getUTCMinutes = function() {
        return this.date.getUTCMinutes()
    }
    ;
    g.h.add = function(a) {
        g.ko.prototype.add.call(this, a);
        a.hours && this.date.setUTCHours(this.date.getUTCHours() + a.hours);
        a.minutes && this.date.setUTCMinutes(this.date.getUTCMinutes() + a.minutes);
        a.seconds && this.date.setUTCSeconds(this.date.getUTCSeconds() + a.seconds)
    }
    ;
    g.h.xo = function(a) {
        var b = g.ko.prototype.xo.call(this, a);
        return a ? b + " " + g.yb(this.getHours(), 2) + ":" + g.yb(this.getMinutes(), 2) + ":" + g.yb(this.getSeconds(), 2) : b + "T" + g.yb(this.getHours(), 2) + g.yb(this.getMinutes(), 2) + g.yb(this.getSeconds(), 2)
    }
    ;
    g.h.toString = function() {
        return this.xo()
    }
    ;
    g.h.clone = function() {
        var a = new d2(this.date);
        a.eo = this.eo;
        a.ho = this.ho;
        return a
    }
    ;
    var e2 = {}
      , f2 = null;
    g.A(Rva, g.bf);
    g.A(h2, g.ut);
    g.h = h2.prototype;
    g.h.getDuration = function() {
        return this.duration
    }
    ;
    g.h.play = function(a) {
        if (a || 0 == this.g)
            this.progress = 0,
            this.coords = this.l;
        else if (this.ob())
            return !1;
        g2(this);
        this.startTime = a = (0,
        g.F)();
        -1 == this.g && (this.startTime -= this.duration * this.progress);
        this.endTime = this.startTime + this.duration;
        this.A = this.startTime;
        this.progress || this.Bp();
        this.Fe("play");
        -1 == this.g && this.Fe("resume");
        this.g = 1;
        var b = g.Fa(this);
        b in e2 || (e2[b] = this);
        Pva();
        Qva(this, a);
        return !0
    }
    ;
    g.h.stop = function(a) {
        g2(this);
        this.g = 0;
        a && (this.progress = 1);
        Sva(this, this.progress);
        this.Fe("stop");
        this.xm()
    }
    ;
    g.h.pause = function() {
        this.ob() && (g2(this),
        this.g = -1,
        this.Fe("pause"))
    }
    ;
    g.h.V = function() {
        0 == this.g || this.stop(!1);
        this.Fe("destroy");
        h2.ba.V.call(this)
    }
    ;
    g.h.destroy = function() {
        this.dispose()
    }
    ;
    g.h.Mu = function() {
        this.Fe("animate")
    }
    ;
    g.h.Fe = function(a) {
        this.dispatchEvent(new Rva(a,this))
    }
    ;
    g.A(i2, h2);
    i2.prototype.o = g.y;
    i2.prototype.Mu = function() {
        this.o();
        i2.ba.Mu.call(this)
    }
    ;
    i2.prototype.xm = function() {
        this.o();
        i2.ba.xm.call(this)
    }
    ;
    i2.prototype.Bp = function() {
        this.o();
        i2.ba.Bp.call(this)
    }
    ;
    g.A(j2, i2);
    j2.prototype.o = function() {
        this.element.style.left = Math.round(this.coords[0]) + "px";
        this.element.style.top = Math.round(this.coords[1]) + "px"
    }
    ;
    var M3 = {};
    g.A(u2, g.Df);
    g.h = u2.prototype;
    g.h.xs = null;
    g.h.Fl = null;
    g.h.la = function() {
        return this.Fl
    }
    ;
    g.h.addEventListener = function(a, b, c, d) {
        g.pf(this.Fl, a, b, c, d)
    }
    ;
    g.h.removeEventListener = function(a, b, c, d) {
        g.xf(this.Fl, a, b, c, d)
    }
    ;
    g.h.V = function() {
        u2.ba.V.call(this);
        var a = this.Fl;
        if (a)
            if (g.hf(a))
                a.Ge && g.mf(a.Ge);
            else if (a = g.sf(a)) {
                var b = 0, c;
                for (c in a.listeners)
                    for (var d = a.listeners[c].concat(), e = 0; e < d.length; ++e)
                        g.yf(d[e]) && ++b
            }
    }
    ;
    g.A(y2, u2);
    y2.prototype.fill = null;
    g.A(fwa, u2);
    z2.prototype.Pf = null;
    z2.prototype.ae = null;
    z2.prototype.Jm = !0;
    var gwa = [2, 2, 6, 6, 0];
    g.h = z2.prototype;
    g.h.clear = function() {
        this.sb.length = 0;
        this.Ka.length = 0;
        this.Be.length = 0;
        delete this.Pf;
        delete this.ae;
        delete this.Jm;
        return this
    }
    ;
    g.h.moveTo = function(a, b) {
        0 == g.Ka(this.sb) ? this.Be.length -= 2 : (this.sb.push(0),
        this.Ka.push(1));
        this.Be.push(a, b);
        this.ae = this.Pf = [a, b];
        return this
    }
    ;
    g.h.Ec = function(a) {
        var b = g.Ka(this.sb);
        if (null == b)
            throw Error("Path cannot start with lineTo");
        1 != b && (this.sb.push(1),
        this.Ka.push(0));
        for (b = 0; b < arguments.length; b += 2) {
            var c = arguments[b]
              , d = arguments[b + 1];
            this.Be.push(c, d)
        }
        this.Ka[this.Ka.length - 1] += b / 2;
        this.ae = [c, d]
    }
    ;
    g.h.close = function() {
        var a = g.Ka(this.sb);
        if (null == a)
            throw Error("Path cannot start with close");
        4 != a && (this.sb.push(4),
        this.Ka.push(1),
        this.ae = this.Pf);
        return this
    }
    ;
    g.h.clone = function() {
        var a = new this.constructor;
        a.sb = this.sb.concat();
        a.Ka = this.Ka.concat();
        a.Be = this.Be.concat();
        a.Pf = this.Pf && this.Pf.concat();
        a.ae = this.ae && this.ae.concat();
        a.Jm = this.Jm;
        return a
    }
    ;
    g.h.transform = function(a) {
        if (!this.Jm)
            throw Error("Non-simple path");
        a.transform(this.Be, 0, this.Be, 0, this.Be.length / 2);
        this.Pf && a.transform(this.Pf, 0, this.Pf, 0, 1);
        this.ae && this.Pf != this.ae && a.transform(this.ae, 0, this.ae, 0, 1);
        return this
    }
    ;
    g.h.isEmpty = function() {
        return 0 == this.sb.length
    }
    ;
    g.A(iwa, y2);
    B2.prototype.Bb = function() {
        return this.l
    }
    ;
    g.A(C2, g.Tu);
    C2.prototype.B = null;
    C2.prototype.Hj = function() {
        return this.Re ? g.Kh(this.la()) : g.ua(this.width) && g.ua(this.height) ? new g.I(this.width,this.height) : null
    }
    ;
    C2.prototype.resume = function() {}
    ;
    g.A(w2, ewa);
    g.A(v2, ewa);
    g.A(D2, fwa);
    D2.prototype.clear = function() {
        g.Id(this.la())
    }
    ;
    D2.prototype.setSize = function(a, b) {
        var c = this.la(), d = {
            width: a,
            height: b
        }, e;
        for (e in d)
            c.setAttribute(e, d[e])
    }
    ;
    g.A(jwa, iwa);
    var G2;
    g.A(E2, C2);
    var lwa = 0;
    g.h = E2.prototype;
    g.h.zo = function() {
        var a = x2(this, "svg", {
            width: this.width,
            height: this.height,
            overflow: "hidden"
        })
          , b = x2(this, "g");
        this.F = x2(this, "defs");
        this.B = new D2(b,this);
        a.appendChild(this.F);
        a.appendChild(b);
        this.g = a;
        this.o && (this.la().setAttribute("preserveAspectRatio", "none"),
        this.M ? this.xq() : this.la().setAttribute("viewBox", "0 0 " + (this.o ? this.o + " " + this.K : "")))
    }
    ;
    g.h.xq = function() {
        if (this.Re) {
            var a = this.Hj();
            if (0 == a.width)
                this.la().style.visibility = "hidden";
            else {
                this.la().style.visibility = "";
                var b = a.width / this.o;
                a = a.height / this.K;
                this.B.la().setAttribute("transform", "scale(" + b + " " + a + ") translate(0 0)")
            }
        }
    }
    ;
    g.h.setSize = function(a, b) {
        g.Jh(this.la(), a, b)
    }
    ;
    g.h.Hj = function() {
        if (!g.rh)
            return this.Re ? g.Kh(this.la()) : E2.ba.Hj.call(this);
        var a = this.width
          , b = this.height
          , c = g.u(a) && -1 != a.indexOf("%")
          , d = g.u(b) && -1 != b.indexOf("%");
        if (!this.Re && (c || d))
            return null;
        if (c) {
            var e = this.la().parentNode;
            var f = g.Kh(e);
            a = (0,
            window.parseFloat)(a) * f.width / 100
        }
        d && (e = e || this.la().parentNode,
        f = f || g.Kh(e),
        b = (0,
        window.parseFloat)(b) * f.height / 100);
        return new g.I(a,b)
    }
    ;
    g.h.clear = function() {
        this.B.clear();
        g.Id(this.F);
        this.l = {}
    }
    ;
    g.h.yj = function() {
        var a = this.Hj();
        E2.ba.yj.call(this);
        a || this.dispatchEvent("resize");
        if (this.M) {
            a = this.width;
            var b = this.height;
            "string" == typeof a && -1 != a.indexOf("%") && "string" == typeof b && -1 != b.indexOf("%") && this.I.U(mwa(), "tick", this.xq);
            this.xq()
        }
    }
    ;
    g.h.rl = function() {
        E2.ba.rl.call(this);
        this.M && this.I.Ea(mwa(), "tick", this.xq)
    }
    ;
    g.h.V = function() {
        delete this.l;
        delete this.F;
        delete this.B;
        this.I.dispose();
        delete this.I;
        E2.ba.V.call(this)
    }
    ;
    g.q(K2, g.oV);
    g.h = K2.prototype;
    g.h.load = function() {
        g.oV.prototype.load.call(this);
        if (!L2(g.Y(this.g).playerStyle)) {
            var a = this.g.getVideoData();
            if (a = a.dj ? null : a.rb && a.rb.endscreen && a.rb.endscreen.endscreenRenderer || null)
                a = J2(a, pwa(this)),
                M2(this, a);
            else {
                var b = this.g.getVideoData();
                a = b.videoId;
                this.B && this.B.abort();
                a = {
                    method: "POST",
                    wd: (0,
                    g.z)(this.QR, this, a),
                    bd: {
                        v: a
                    },
                    withCredentials: !0
                };
                "gaming" == g.Y(this.g).playerStyle && (a.bd.gaming = "1");
                this.M && (a.bd.ptype = "embedded");
                var c = this.g.getVideoData().Oy;
                c && (a.Nb = {
                    ad_tracking: c
                });
                if (b = g.PO(b))
                    if (b = g.Mc(b),
                    b = g.Jc(b))
                        this.B = g.oE(b, a)
            }
        }
    }
    ;
    g.h.unload = function() {
        M2(this, null);
        this.B && (this.B.abort(),
        this.B = null);
        g.oV.prototype.unload.call(this)
    }
    ;
    g.h.OM = function(a, b) {
        if (!L2(g.Y(this.g).playerStyle))
            return null;
        if ("loadCustomEndscreenRenderer" == a) {
            var c = J2(b, "new");
            M2(this, c);
            return !0
        }
        return null
    }
    ;
    g.h.PM = function() {
        return L2(g.Y(this.g).playerStyle) ? ["loadCustomEndscreenRenderer"] : []
    }
    ;
    g.h.QR = function(a, b) {
        var c = this.B = null;
        if (200 == b.status) {
            var d = b.responseText;
            ")]}" == d.substring(0, 3) && (d = d.substring(3),
            c = JSON.parse(d),
            c = J2(c, pwa(this)))
        }
        M2(this, c)
    }
    ;
    g.h.aB = function() {
        if (this.o && this.o.elements) {
            var a = this.g.sB();
            if (a && 0 != a.width && 0 != a.height) {
                var b = this.g.nu();
                if (b && 0 != b.width && 0 != b.height) {
                    var c = a.width / a.height;
                    var d = 0;
                    for (var e = -1, f = 0; f < yza.length; f++) {
                        var k = Math.abs(b.width - yza[f]);
                        if (-1 == e || d >= k)
                            e = f,
                            d = k
                    }
                    d = zza[e];
                    this.A && g.ph(this.A.element, "outline-width", Math.max(b.width, b.height) + "px");
                    for (b = 0; b < this.o.elements.length; ++b)
                        if (f = this.o.elements[b].id,
                        e = this.l[f],
                        k = this.F[f],
                        e && k) {
                            var l = k.width * c / k.aspectRatio;
                            f = Math.round(l * a.height);
                            var m = a.left + Math.round(k.left * a.width)
                              , n = a.top + Math.round(k.top * a.height);
                            g.Jh(e.element, Math.round(k.width * a.width), f);
                            g.wh(e.element, m, n);
                            g.nq(e.element, Aza);
                            m = k.left + k.width / 2;
                            k = k.top + l / 2;
                            g.S(e.element, .5 >= m && .5 >= k ? "ytp-ce-top-left-quad" : .5 < m && .5 >= k ? "ytp-ce-top-right-quad" : .5 >= m && .5 < k ? "ytp-ce-bottom-left-quad" : "ytp-ce-bottom-right-quad");
                            g.nq(e.element, zza);
                            g.S(e.element, d);
                            (e = g.qd(window.document, "div", "ytp-ce-expanding-overlay-body", e.element)[0]) && g.ph(e, "height", f + "px")
                        }
                }
            }
        }
    }
    ;
    g.h.QM = function(a) {
        if (this.o)
            if ("ytp-ce-in-endscreen" == a.getId())
                this.J = !1,
                this.o.skip && 1 == this.g.Bh() ? (g.SU(this.g, !0),
                this.g.Zb(window.Infinity),
                this.J = !0) : (P2(this, this.o.impressionUrls),
                (a = g.PG()) && H2(a, this.o.visualElement));
            else if (!this.J) {
                a = a.getId().substring(15);
                var b = this.l[a]
                  , c = this.F[a];
                g.S(b.element, "ytp-ce-element-show");
                b.element.removeAttribute("aria-hidden");
                b = this.g.getRootNode();
                g.S(b, "ytp-ce-shown");
                P2(this, c.impressionUrls);
                (b = g.PG()) && H2(b, c.visualElement);
                g.Y(this.g).yb && this.g.va("endscreenelementshown", a)
            }
    }
    ;
    g.h.RM = function(a) {
        if ("ytp-ce-in-endscreen" != a.getId() && !this.J) {
            a = a.getId().substring(15);
            var b = this.l[a];
            g.mq(b.element, "ytp-ce-element-show");
            b.element.setAttribute("aria-hidden", !0);
            b = this.g.getRootNode();
            g.mq(b, "ytp-ce-shown");
            g.Y(this.g).yb && this.g.va("endscreenelementhidden", a)
        }
    }
    ;
    g.h.bV = function(a) {
        var b = this;
        a.target === window && (new g.Yt(function() {
            for (var a in b.l)
                g.nq(b.l[a].element, ["ytp-ce-force-expand", "ytp-ce-element-hover", "ytp-ce-element-shadow-show"])
        }
        ,0)).start()
    }
    ;
    g.h.OC = function(a, b) {
        if (a.targetUrl && (!b || "keypress" != b.type || 13 == b.keyCode)) {
            for (var c = b.target; c && !g.kq(c, "ytp-ce-element"); ) {
                g.kq(c, "subscribe-label") && Q2(this, a);
                if (g.kq(c, "ytp-ce-channel-subscribe"))
                    return;
                c = g.Pd(c)
            }
            if (!c || g.kq(c, "ytp-ce-element-hover")) {
                b.preventDefault();
                b.stopPropagation();
                if (c = this.l[a.id])
                    this.Vu(c, a),
                    c.element.blur();
                b.ctrlKey || b.metaKey || "new" == a.nq ? (Q2(this, a),
                this.iE(),
                this.g.kd(),
                c = g.Mc(a.targetUrl),
                c = g.Jc(c),
                g.rV(c, void 0, a.Rd)) : (c = this.g.getVideoData().isDni,
                c = (0,
                g.z)(this.fW, this, c, a.targetUrl, a.Rd),
                c = (0,
                g.z)(this.iE, this, c),
                Q2(this, a, c))
            }
        }
    }
    ;
    g.h.fW = function(a, b, c) {
        var d = g.eE(b);
        a && d && (d.v || d.list) ? g.F0(this.g.app, d.v, c, d.list, !1, void 0, void 0) : g.qV(b, c)
    }
    ;
    g.h.Yt = function(a, b) {
        g.kq(a.element, "ytp-ce-element-hover") || ("VIDEO" == b.type || "PLAYLIST" == b.type ? g.S(a.element, "ytp-ce-element-hover") : g.Y(this.g).l ? (new g.Yt(function() {
            g.S(a.element, "ytp-ce-element-hover")
        }
        ,200)).start() : g.S(a.element, "ytp-ce-element-hover"),
        P2(this, b.YK),
        O2(this, b.id, !0))
    }
    ;
    g.h.Vu = function(a, b) {
        g.mq(a.element, "ytp-ce-element-hover");
        g.mq(a.element, "ytp-ce-force-expand");
        O2(this, b.id, !1)
    }
    ;
    g.h.iE = function(a) {
        this.g.Oi(17, a)
    }
    ;
    var yza = [346, 426, 470, 506, 570, 640, 853, 1280, 1920]
      , zza = "ytp-ce-size-346 ytp-ce-size-426 ytp-ce-size-470 ytp-ce-size-506 ytp-ce-size-570 ytp-ce-size-640 ytp-ce-size-853 ytp-ce-size-1280 ytp-ce-size-1920".split(" ")
      , Aza = ["ytp-ce-top-left-quad", "ytp-ce-top-right-quad", "ytp-ce-bottom-left-quad", "ytp-ce-bottom-right-quad"];
    var Awa = {
        wZ: "current",
        OH: "new"
    };
    var Cwa = {
        CLOSE: "close",
        I1: "openUrl",
        tI: "subscribe"
    }
      , Dwa = {
        Rq: "click",
        CLOSE: "close",
        K_: "hidden",
        E2: "rollOut",
        fI: "rollOver",
        jI: "shown"
    };
    var Gwa = {
        W4: "xx",
        X4: "xy",
        d5: "yx",
        e5: "yy"
    };
    g.q(Mwa, c3);
    var Swa = {
        AG: "anchored",
        q2: "rect",
        R2: "shapeless"
    };
    var Xwa = {
        CLOSED: "closed",
        R1: "playerControlShow",
        fI: "rollOver",
        jI: "shown"
    };
    e3.prototype.Ta = function() {
        var a = fxa(this, function(a) {
            return "openUrl" == a.type && null != a.url
        });
        return a ? a.url : null
    }
    ;
    e3.prototype.showLinkIcon = function() {
        return g3(this, function(a) {
            return null != a.url && a.url.showLinkIcon
        })
    }
    ;
    var f3 = {
        AG: "anchored",
        DG: "branding",
        CHANNEL: "channel",
        vZ: "cta",
        N_: "highlightText",
        q0: "label",
        PLAYLIST: "playlist",
        c2: "popup",
        h3: "speech",
        tI: "subscribe",
        I3: "title",
        VIDEO: "video",
        M4: "vote",
        P4: "website"
    }
      , bxa = {
        DG: "branding",
        SY: "card",
        XZ: "drawer",
        M_: "highlight",
        N0: "marker",
        h2: "promotion",
        TEXT: "text",
        S4: "widget"
    }
      , cxa = {
        H4: "video_relative",
        S1: "player_relative"
    };
    lxa.prototype.A = function(a, b, c, d) {
        this.g[a] = b ? !c : c;
        a = g.Ob(this.g, function(a) {
            return a
        });
        this.o != a && (this.o = a,
        this.l.R(this.B, a, d))
    }
    ;
    j3.prototype.la = function() {
        return this.B
    }
    ;
    j3.prototype.o = function() {}
    ;
    var n3 = {
        bevel: 1,
        dropshadow: 2
    };
    g.q(p3, j3);
    p3.prototype.o = function(a, b) {
        var c = i3(a);
        if (c) {
            var d = d3(c, b);
            if (!(0 >= d.width || 0 >= d.height)) {
                var e;
                if (e = (c = (c = ixa(a)) && c.g ? c.g : null) && c.length ? c[0] : null) {
                    var f = g.gh(Iwa(b, Jwa(e, new g.bh(e.F,e.H,e.Rk,e.o), b.g)))
                      , k = d.clone();
                    c = new g.bh(f.x,f.y,1,1);
                    var l = Math.max(k.left + k.width, c.left + c.width)
                      , m = Math.max(k.top + k.height, c.top + c.height);
                    k.left = Math.min(k.left, c.left);
                    k.top = Math.min(k.top, c.top);
                    k.width = l - k.left;
                    k.height = m - k.top;
                    c = a.l;
                    k = o3(k, c.effects);
                    l = k3(this, k.width, k.height);
                    m = txa(c, k.width, k.height, this.g);
                    d = new g.bh(d.left - k.left,d.top - k.top,d.width,d.height);
                    var n = new g.hd(f.x - k.left,f.y - k.top);
                    this.A = 17 * b3(b.g, e.l, e.g ? e.g : "xy");
                    e = c.cornerRadius;
                    f = a.l;
                    var p = this.g && h3(a)
                      , r = p ? f.borderWidth + 1 : f.borderWidth;
                    p = (f = r ? new B2(r,p ? f.l : f.borderColor) : null) ? f.Bb() / 2 : 0;
                    r = vxa(d, n);
                    var v = this.C(d, e, n, r)
                      , D = n.x;
                    n = n.y;
                    var H = d.width
                      , L = d.height
                      , T = d.left;
                    d = d.top;
                    var ia = new z2;
                    ia.moveTo(T + e + p, d + p);
                    "t" == r && (ia.Ec(v.start, d + p),
                    ia.Ec(D, n),
                    ia.Ec(v.end, d + p));
                    ia.Ec(T + H - e - p, d + p);
                    A2(ia, e, e, -90);
                    "r" == r && (ia.Ec(T + H - p, v.start),
                    ia.Ec(D, n),
                    ia.Ec(T + H - p, v.end));
                    ia.Ec(T + H - p, d + L - e - p);
                    A2(ia, e, e, 0);
                    "b" == r && (ia.Ec(v.end, d + L - p),
                    ia.Ec(D, n),
                    ia.Ec(v.start, d + L - p));
                    ia.Ec(T + e + p, d + L - p);
                    A2(ia, e, e, 90);
                    "l" == r && (ia.Ec(T + p, v.end),
                    ia.Ec(D, n),
                    ia.Ec(T + p, v.start));
                    ia.Ec(T + p, d + e + p);
                    A2(ia, e, e, 180);
                    ia.close();
                    F2(l, ia, f, m);
                    if (m = this.la())
                        g.S(m, "annotation-shape"),
                        g.S(m, "annotation-speech-shape"),
                        g.wh(m, k.left, k.top),
                        g.Jh(m, k.width, k.height),
                        uxa(l, m, c.effects)
                }
            }
        }
    }
    ;
    p3.prototype.C = function(a, b, c, d) {
        function e(a, c, d, e) {
            a = Math.min(Math.max(e - 2 * b, 0), a);
            c = g.dd(c - a / 2, d + b, d + e - a - b);
            return new b2(c,c + a)
        }
        return "t" == d || "b" == d ? e(this.A, c.x, a.left, a.width) : "l" == d || "r" == d ? e(this.A, c.y, a.top, a.height) : new b2(0,0)
    }
    ;
    g.q(q3, j3);
    q3.prototype.o = function(a, b) {
        var c = i3(a);
        if (c) {
            var d = d3(c, b);
            if (!(0 >= d.width || 0 >= d.height)) {
                var e = a.l;
                c = o3(d, e.effects);
                var f = k3(this, c.width, c.height)
                  , k = new g.bh(0,0,d.width,d.height)
                  , l = e.cornerRadius;
                d = new B2(!e.o && this.g ? 1 : e.o,e.bgColor);
                var m = new v2("#000",0)
                  , n = d ? d.Bb() / 2 + 1 : 0;
                k = sxa(k, l, n);
                F2(f, k, d, m);
                f = this.la();
                g.S(f, "annotation-shape");
                e = e.g;
                g.Mh(f, this.g ? Math.max(e, .9) : e);
                g.wh(f, c.left, c.top);
                g.Jh(f, c.width, c.height)
            }
        }
    }
    ;
    g.q(r3, j3);
    r3.prototype.o = function(a, b) {
        var c = i3(a);
        if (c) {
            var d = d3(c, b);
            if (!(0 >= d.width || 0 >= d.height)) {
                c = a.l;
                var e = o3(d, c.effects)
                  , f = k3(this, e.width, e.height)
                  , k = new g.bh(0,0,d.width,d.height);
                d = txa(c, d.width, d.height, this.g);
                var l = c.cornerRadius;
                var m = a.l;
                var n = this.g && h3(a)
                  , p = n ? m.borderWidth + 1 : m.borderWidth;
                n = (m = p ? new B2(p,n ? m.l : m.borderColor) : null) ? m.Bb() / 2 + 1 : 0;
                k = sxa(k, l, n);
                F2(f, k, m, d);
                if (k = this.la())
                    g.S(k, "annotation-shape"),
                    g.S(k, "annotation-popup-shape"),
                    g.wh(k, e.left, e.top),
                    g.Jh(k, e.width, e.height),
                    uxa(f, k, c.effects)
            }
        }
    }
    ;
    g.q(s3, p3);
    s3.prototype.C = function(a, b, c, d) {
        function e(a, c, d, e) {
            a = Math.min(Math.max(e - 2 * b, 0), a);
            c = c <= d + e / 2 ? Math.max(d + e / 4 - a / 2, d + b) : Math.min(d + 3 * e / 4 - a / 2, d + e - a - b);
            return new b2(c,c + a)
        }
        return "t" == d || "b" == d ? e(this.A, c.x, a.left, a.width) : "l" == d || "r" == d ? e(this.A, c.y, a.top, a.height) : new b2(0,0)
    }
    ;
    g.q(t3, g.M);
    g.h = t3.prototype;
    g.h.FB = function() {
        this.H || (this.A && g.O(this.A, !0),
        this.B && g.O(this.B, !0),
        this.o && (this.o.g = !0,
        g.Mh(this.l, x3(this) ? 1 : 0),
        this.o.o(this.g, v3(this))),
        this.F.isActive() && this.F.stop(),
        this.H = !0,
        this.I = this.W.xh(g.QU(this.C), "mouseleave", function(a) {
            this.xp.stop();
            this.Ih(a)
        }))
    }
    ;
    g.h.Ih = function() {
        this.H && (this.M ? this.F.start() : this.Oz(),
        this.o && (this.o.g = !1,
        g.Mh(this.l, x3(this) ? 1 : 0),
        this.o.o(this.g, v3(this))),
        this.H = !1,
        this.I && (this.W.Ea(this.I),
        this.I = null))
    }
    ;
    g.h.Oz = function() {
        this.A && g.O(this.A, !1);
        this.B && g.O(this.B, !1)
    }
    ;
    g.h.WO = function(a) {
        this.Y = a;
        this.xp.Kj()
    }
    ;
    g.h.VO = function() {
        var a = this.Y
          , b = new g.hd(a.clientX,a.clientY)
          , c = g.Ch(this.C.getRootNode())
          , d = zxa(c, this.K);
        c = (this.B && g.Nh(this.B) || this.A && g.Nh(this.A)) && zxa(c, this.M);
        d && d.contains(b) || c && c.contains(b) ? this.FB(a) : this.Ih(a)
    }
    ;
    g.h.show = function() {
        var a = this.g.l;
        a = (a && 0 == a.g || "title" == this.g.style || "highlightText" == this.g.style ? !1 : !0) && !this.o;
        var b = !this.l
          , c = "widget" == this.g.type;
        if (a) {
            var d = v3(this)
              , e = null;
            "highlight" == this.g.type || "label" == this.g.style ? e = new q3 : "popup" == this.g.style ? e = new r3 : "anchored" == this.g.style ? e = new p3 : "speech" == this.g.style && (e = new s3);
            e && (e.o(this.g, d),
            this.o = e,
            d = e.la()) && (g.O(d, !1),
            g.S(d, "annotation-type-" + this.g.type.toLowerCase()),
            this.X(d))
        }
        if (b) {
            d = ["annotation"];
            "highlightText" != this.g.style || d.push("annotation-no-mouse");
            d.push("annotation-type-" + this.g.type.toLowerCase());
            this.l = g.K("DIV", d);
            g.O(this.l, !1);
            this.g.A && (this.D = g.K("DIV", "inner-text"),
            "label" == this.g.style && (g.S(this.D, "label-text"),
            this.D.style.backgroundColor = this.g.l.bgColor),
            g.Sd(this.D, this.g.A),
            this.l.appendChild(this.D));
            g.bF(this.l, "annotation_id", this.g.id);
            this.X(this.l);
            wxa(this, this.l);
            if (h3(this.g) && this.g.showLinkIcon()) {
                if (e = this.g.Ta())
                    d = this.l,
                    e = new g.wm(a3(e)),
                    d.title = e.l + e.o;
                this.B = g.K("SPAN", "annotation-link-icon");
                g.O(this.B, !1);
                this.l.appendChild(this.B)
            }
            xxa(this);
            h3(this.g) || (this.l.style.cursor = "default")
        }
        c && "subscribe" == this.g.style && g.J("yt-uix-subscription-button", this.l);
        if (a || b) {
            a: {
                a = this.g.segment.g;
                if (a.length && (a = Uwa(a[0]))) {
                    a = a.B;
                    break a
                }
                a = 0
            }
            this.l && (this.l.style.zIndex = a);
            this.o && this.o.la() && (this.o.la().style.zIndex = a)
        }
        g.O(this.l, !0);
        g.Mh(this.l, x3(this) ? 1 : 0);
        w3(this);
        this.o && this.o.la() && g.O(this.o.la(), !0)
    }
    ;
    g.h.hide = function() {
        this.l && g.O(this.l, !1);
        this.o && this.o.la() && g.O(this.o.la(), !1);
        this.J && (this.W.Ea(this.J),
        this.J = null)
    }
    ;
    g.q(y3, g.M);
    g.h = y3.prototype;
    g.h.hide = function() {
        this.isVisible = !1;
        this.view && (Bxa(this),
        this.view.hide())
    }
    ;
    g.h.show = function() {
        this.isVisible = !0;
        this.view && (this.view.show(),
        this.l.subscribe("resize", this.EB, this),
        this.l.subscribe("onVideoAreaChange", this.tD, this))
    }
    ;
    g.h.destroy = function() {
        if (this.view) {
            Bxa(this);
            var a = this.view;
            g.EF(a.W);
            a.xp.dispose();
            a.F.dispose();
            a.l && g.Kd(a.l);
            a.o && a.o.la() && g.Kd(a.o.la())
        }
        z3(this)
    }
    ;
    g.h.tD = function() {
        w3(this.view)
    }
    ;
    g.h.EB = function() {
        w3(this.view)
    }
    ;
    g.q(Cxa, t2);
    g.q(A3, t2);
    g.q(Dxa, A3);
    g.q(Exa, t2);
    g.q(Fxa, t2);
    g.q(Hxa, A3);
    g.q(Ixa, A3);
    g.q(Jxa, t2);
    g.q(B3, t2);
    K3.prototype.o = function(a, b) {
        var c = g.Zd(b.target, "label");
        c && g.U(c, "iv-card-poll-choice-focused", a)
    }
    ;
    K3.prototype.D = function(a, b) {
        var c = g.$d(b.target, "iv-card-poll");
        if (c)
            if (a.A)
                g.J("iv-card-sign-in-button", c).click();
            else {
                var d = (0,
                window.parseInt)(c2(b.target, "pollChoiceIndex"), 10);
                if (null == a.g)
                    a.choices[d].count++,
                    a.g = d;
                else if (a.g != d) {
                    var e = a.choices[a.g];
                    e.count = Math.max(e.count - 1, 0);
                    a.choices[d].count++;
                    a.g = d
                } else
                    e = a.choices[a.g],
                    e.count = Math.max(e.count - 1, 0),
                    a.g = null;
                Oxa(a, c);
                g.sE(this.g.videoData.Mf, {
                    bd: {
                        action_poll_vote: 1
                    },
                    Nb: {
                        poll_id: a.id,
                        index: d,
                        session_token: a.H
                    }
                });
                G3(this.g.logger, a.o, void 0, {
                    "link-id": d
                }, a.l.click, 5);
                (c = g.PG()) && I2(c, a.C)
            }
    }
    ;
    K3.prototype.C = function(a) {
        var b = g.x("yt.www.ypc.bootstrap.api.loadOffersForInnertubeRequestParams"), c;
        b && (c = function() {
            b(a.H, a.B)
        }
        );
        Sxa(this, a, !0, c)
    }
    ;
    K3.prototype.F = function(a) {
        var b = g.x("yt.www.ypc.bootstrap.api.loadOffers");
        b && (b = g.Ga(b, a.K, a.H, a.B));
        Sxa(this, a, !1, b)
    }
    ;
    g.q(U3, g.M);
    g.h = U3.prototype;
    g.h.hq = function() {
        this.context.o.subscribe("resize", this.Fn, this)
    }
    ;
    g.h.la = function() {
        return this.Ga
    }
    ;
    g.h.aj = function(a, b, c, d, e, f, k) {
        this.context.g.U(a, "click", g.Ga(this.Vo, b, c, d, e, f || [], k || 0), this);
        this.context.g.U(a, "touchstart", g.Ga(function() {
            this.cw = !1
        }), this);
        this.context.g.U(a, "touchmove", g.Ga(function() {
            this.cw = !0
        }), this)
    }
    ;
    g.h.Vo = function(a, b, c, d, e, f, k) {
        if (this.cw)
            return !1;
        k && (k.stopPropagation(),
        k.preventDefault());
        $xa(this, a, c, d, e, f);
        return !1
    }
    ;
    g.h.show = function() {
        this.Y = (0,
        g.F)()
    }
    ;
    g.h.hide = function() {}
    ;
    g.h.destroy = function() {
        g.Kd(this.la())
    }
    ;
    g.h.Fn = function() {}
    ;
    g.q(Y3, U3);
    g.h = Y3.prototype;
    g.h.isAvailable = function() {
        var a;
        if (a = !!this.o.length)
            (a = this.g.getRootNode()) ? (a = g.Kh(a),
            a = 173 < a.width && 173 < a.height) : a = !1;
        return a
    }
    ;
    g.h.Fn = function() {
        var a = this.isAvailable();
        g.O(this.la(), a);
        g.U(this.context.l.getRootNode(), "ytp-iv-drawer-enabled", a);
        Y1(this.g)
    }
    ;
    g.h.destroy = function() {
        var a = g.IU(this.g).B;
        a && a.Wt(!1, void 0);
        this.g.getRootNode().removeChild(this.D);
        g.WF(this.ha);
        g.tF(this.X);
        this.Z && this.Z.dispose();
        this.F && this.F.dispose();
        U3.prototype.destroy.call(this)
    }
    ;
    g.h.lN = function(a) {
        this.J.start();
        a.preventDefault();
        a = a || window.event;
        var b = 0;
        "MozMousePixelScroll" == a.type ? b = 0 == (a.axis == a.HORIZONTAL_AXIS) ? a.detail : 0 : window.opera ? b = a.detail : b = 0 == a.wheelDelta % 120 ? "WebkitTransform"in window.document.documentElement.style ? window.chrome && 0 == window.navigator.platform.indexOf("Mac") ? a.wheelDeltaY / -30 : a.wheelDeltaY / -1.2 : a.wheelDelta / -1.6 : a.wheelDeltaY / -3;
        if (a = b)
            this.A.scrollTop += a
    }
    ;
    g.h.sE = function(a) {
        if (!g.kq(this.g.getRootNode(), "ytp-cards-teaser-shown")) {
            this.l != a && (this.l = a,
            Z3(this));
            if (g.Nh(this.la())) {
                if (2 == this.context.l.Oa())
                    var b = 1 == ((0,
                    window.isNaN)(void 0) ? this.context.l.lB() : void 0);
                else
                    b = (0,
                    window.isNaN)(void 0) ? this.context.l.Bh() : void 0,
                    b = 1 == b || 0 == b && 0 === this.context.l.getCurrentTime();
                if (b && a.gc.teaserDurationMs) {
                    b = {
                        teaserText: a.gc.teaserText,
                        durationMs: a.gc.teaserDurationMs
                    };
                    var c = g.IU(this.g).B;
                    c && c.Wt(!0, b)
                }
            }
            this.oa.isActive() || ((!this.B || !this.J.isActive() && this.T) && jya(this, a),
            this.oa.start(910 + a.gc.teaserDurationMs))
        }
    }
    ;
    g.h.WV = function(a) {
        this.B || (this.l = a,
        Z3(this),
        jya(this, a),
        W3(this, "YOUTUBE_DRAWER_AUTO_OPEN", !1, a))
    }
    ;
    g.h.qJ = function() {
        if (this.B) {
            I3(this.context.logger, this.M, 4, $3(this).l.close);
            var a = g.PG();
            a && this.I && I2(a, this.I);
            X3(this)
        }
    }
    ;
    g.h.BJ = function() {
        g.U(this.D, "iv-drawer-scrolled", 0 < this.A.scrollTop)
    }
    ;
    g.h.kM = function() {
        var a = $3(this);
        J3(this.context.logger, 8, a.o, a.l.pW);
        var b = g.PG();
        b && a && (H2(b, a.F),
        H2(b, a.D))
    }
    ;
    g.h.jM = function(a) {
        var b = $3(this)
          , c = g.PG();
        this.l ? a ? (a = this.context.logger,
        J3(a, 9, b.o, b.l.Hv),
        a.g.Oi(4, void 0),
        c && I2(c, b.F)) : (a = this.context.logger,
        J3(a, 12, b.o, b.l.Hv),
        a.g.Oi(4, void 0),
        c && I2(c, b.D)) : (a = this.context.logger,
        I3(a, this.M, 12, b.l.Hv),
        a.g.Oi(4, void 0),
        c && this.ga && I2(c, this.ga))
    }
    ;
    var aya = {
        collaborator: Cxa,
        donation: Dxa,
        episode: B3,
        movie: B3,
        playlist: Exa,
        poll: Fxa,
        productListing: Hxa,
        simple: A3,
        tip: Ixa,
        video: Jxa
    };
    g.q(a4, U3);
    a4.prototype.hq = function() {
        U3.prototype.hq.call(this);
        kya(this)
    }
    ;
    g.q(b4, a4);
    g.h = b4.prototype;
    g.h.gW = function(a, b, c, d) {
        this.o.stop();
        if (!this.A) {
            var e = g.Kh(a);
            this.g || (g.Ih(a, e.width),
            g.Ih(b, e.width));
            g.ph(c, "top", e.height - Math.max(Math.min(e.height, d) / 2 + 10, 20) + "px");
            g.ph(c, "right", "1px");
            this.A = !0;
            g.O(a, !0);
            this.B = new g.Yt(function() {
                g.S(this.la(), "iv-branding-active")
            }
            ,0,this);
            this.B.start()
        }
    }
    ;
    g.h.VK = function(a, b) {
        g.mq(this.la(), "iv-branding-active");
        this.C = new g.Yt((0,
        g.z)(function() {
            g.O(a, !1);
            this.g || g.Ih(b, 0)
        }, this),250);
        this.C.start();
        this.A = !1
    }
    ;
    g.h.show = function() {
        if (!this.isActive) {
            a4.prototype.show.call(this);
            if (!this.D) {
                g.S(this.la(), "iv-branding");
                var a = this.annotation.data;
                this.F = a.image_width;
                this.l = g.K("IMG", {
                    src: a.image_url,
                    "class": "branding-img iv-click-target",
                    width: a.image_width,
                    height: a.image_height
                });
                g.O(this.l, !1);
                var b = g.K("DIV", "branding-img-container", this.l);
                this.la().appendChild(b);
                var c = g.K("DIV", "iv-branding-context-name");
                g.Sd(c, a.channel_name);
                var d = g.K("DIV", "iv-branding-context-subscribe");
                if (b = a.standalone_subscribe_button_data)
                    this.g = new g.v_(b.subscribeText,b.subscribeCount,b.unsubscribeText,b.unsubscribeCount,!!b.enabled,!!b.classic,a.channel_id,!!b.subscribed,b.feature,a.session_data.itct,b.signinUrl,this.H.l),
                    this.g.sa(d);
                b = g.K("DIV", "iv-branding-context-subscribe-caret");
                c = g.K("DIV", "branding-context-container-inner", b, c, d);
                g.O(c, !1);
                d = g.K("DIV", "branding-context-container-outer", c);
                g.ph(d, "right", this.F + "px");
                this.la().appendChild(d);
                var e = this.annotation.Ta();
                e && this.aj(this.l, e, this.annotation.id, a.session_data, this.annotation.g);
                this.o = new g.Yt(g.Ga(this.VK, c, d),500,this);
                g.N(this, this.o);
                this.context.g.U(this.la(), "mouseover", (0,
                g.z)(this.gW, this, c, d, b, a.image_height));
                this.context.g.U(this.la(), "mouseout", (0,
                g.z)(this.o.start, this.o, void 0));
                this.D = !0
            }
            F3(this.context.logger, this.annotation.g);
            g.O(this.la(), !0);
            this.isActive = !0;
            if (this.l) {
                a = this.l;
                var f = void 0 === f ? 0 : f;
                b = g.Lh(a).width;
                c = g.ua(void 0) ? void 0 : b;
                g.wh(a, c);
                f = new j2(a,[c, a.offsetTop],[c - b - f, a.offsetTop],200,Tva);
                g.N(this, f);
                this.context.A.U(f, "begin", g.Ga(g.O, a, !0));
                f.play()
            }
        }
    }
    ;
    g.h.hide = function() {
        this.isActive && (g.O(this.la(), !1),
        this.isActive = !1)
    }
    ;
    g.h.destroy = function() {
        this.g && (this.g.dispose(),
        this.g = null);
        a4.prototype.destroy.call(this)
    }
    ;
    g.q(c4, a4);
    g.h = c4.prototype;
    g.h.show = function() {
        this.isActive || (a4.prototype.show.call(this),
        this.I || (lya(this),
        this.I = !0),
        g.O(this.la(), !0),
        this.annotation && this.annotation.g && F3(this.context.logger, this.annotation.g),
        g.tg(function() {
            g.mq(this.la(), "iv-promo-inactive")
        }, 100, this),
        this.la().removeAttribute("aria-hidden"),
        this.isActive = !0,
        d4(this),
        nya(this),
        oya(this, this.F))
    }
    ;
    g.h.hide = function() {
        this.isActive && (g.S(this.la(), "iv-promo-inactive"),
        this.isActive = !1,
        this.la().setAttribute("aria-hidden", !0))
    }
    ;
    g.h.Vo = function(a, b, c, d, e, f, k) {
        return this.B ? !1 : a4.prototype.Vo.call(this, a, b, c, d, e, f, k)
    }
    ;
    g.h.Cs = function(a, b) {
        b.stopPropagation();
        nya(this);
        oya(this, a);
        this.g.focus()
    }
    ;
    g.h.rK = function(a) {
        this.H = !0;
        this.Cs(500, a)
    }
    ;
    g.h.qK = function() {
        this.H = !1;
        mya(this)
    }
    ;
    g.h.XO = function(a) {
        a.stopPropagation();
        this.hide();
        H3(this.context.logger, this.annotation.g)
    }
    ;
    g.h.gK = function(a) {
        a.stopPropagation();
        d4(this);
        this.B = !0;
        g.S(this.la(), "iv-promo-collapsed-no-delay");
        this.C.start();
        H3(this.context.logger, this.annotation.g)
    }
    ;
    g.h.destroy = function() {
        this.C.dispose();
        a4.prototype.destroy.call(this)
    }
    ;
    g.q(e4, g.oV);
    g.h = e4.prototype;
    g.h.IM = function(a, b) {
        if (!qya(g.Y(this.g).playerStyle))
            return null;
        switch (a) {
        case "loadCustomAnnotationsXml":
            var c = g.Vr(b);
            c && g4(this, c);
            return !0;
        case "removeCustomAnnotationById":
            return b && this.l && (cya(this.l, b),
            Y1(this.g)),
            !0
        }
        return null
    }
    ;
    g.h.JM = function() {
        return qya(g.Y(this.g).playerStyle) ? ["loadCustomAnnotationsXml", "removeCustomAnnotationById"] : []
    }
    ;
    g.h.Xu = function() {
        if (this.C) {
            var a = g.T_(g.QU(this.g), !0);
            g.Jh(this.C.element, a.width, a.height);
            g.wh(this.C.element, a.left, a.top)
        }
        if (this.l) {
            var b = g.$U(this.g);
            a = this.l;
            b = b.width;
            g.U(a.D, "iv-drawer-small", 426 >= b);
            g.U(a.D, "iv-drawer-big", 1280 <= b)
        }
    }
    ;
    g.h.pT = function(a) {
        g.Y(this.g).experiments.g("web_player_update_annotations_module_visibility_killswitch") || this.Oc(a.state);
        g.X(a.state, 2) && (this.Wl() && this.ZA() && 2 != this.g.Oa() && this.Xt(!1),
        this.Wt(!1))
    }
    ;
    g.h.load = function() {
        g.oV.prototype.load.call(this);
        g.Y(this.g).experiments.g("web_player_update_annotations_module_visibility_killswitch") ? this.A.show() : this.Oc(g.PU(this.g));
        this.I++;
        var a = this.g.getVideoData()
          , b = a.videoId
          , c = (0,
        g.z)(this.UR, this, b, this.I)
          , d = (0,
        g.z)(function() {
            this.D = null
        }, this);
        g.qG() && (c = xya(this, c));
        c = {
            format: "XML",
            wd: c,
            onError: d,
            bd: {}
        };
        a.isPharma && (c.bd.pharma = "1");
        c.method = "POST";
        c.withCredentials = !0;
        d = g.Y(this.g);
        "gaming" == d.playerStyle && (c.bd.gaming = "1");
        (b = d.C.get(b)) && yya(c, b);
        b = b && (b.bi || b.wr);
        if (!a.xn || b)
            a.Mf ? rya(this, a.Mf, c) : (this.B = (0,
            g.z)(this.NM, this, c),
            this.g.addEventListener("videodatachange", this.B));
        g.hV(this.g, this.C.element, 4);
        this.Xu()
    }
    ;
    g.h.Oc = function(a) {
        a = !g.CP(a) && !g.X(a, 1024);
        g.vH(this.A, a);
        g.vH(this.C, a)
    }
    ;
    g.h.NM = function(a) {
        var b = this.g.getVideoData();
        b.Mf && (this.B && (this.g.removeEventListener("videodatachange", this.B),
        this.B = null),
        rya(this, b.Mf, a))
    }
    ;
    g.h.unload = function() {
        Lxa(this.Ya);
        g.fV(this.g, "annotations_module");
        g.Lb(this.o, function(a) {
            a.destroy()
        });
        g.Lb(this.K, function(a) {
            a.destroy()
        });
        this.H = null;
        this.l && (this.l.destroy(),
        this.l = null,
        Y1(this.g));
        this.J = !1;
        this.D && (this.D.abort(),
        this.D = null);
        this.o = {};
        this.K = {};
        this.A.hide();
        g.oV.prototype.unload.call(this);
        g.uH(this.C);
        this.B && (this.g.removeEventListener("videodatachange", this.B),
        this.B = null)
    }
    ;
    g.h.UR = function(a, b, c) {
        this.D = null;
        if (!tya(this, b, a) && (a = g.XD(c) && c.responseXML ? c.responseXML : null)) {
            g4(this, a);
            g.S(this.g.getRootNode(), "iv-module-loaded");
            a = [];
            for (var d in this.o) {
                b = this.o[d].annotation;
                if (b.segment)
                    if (c = b.segment,
                    c.g.length)
                        if (c = c.g[0].l || c.g[0].g || c.g[0].o,
                        !c || 2 > c.length)
                            c = null;
                        else {
                            var e = c.length - 1;
                            c = 0 >= c[0].t && 0 >= c[e].t ? null : {
                                start: c[0].t,
                                end: c[e].t
                            }
                        }
                    else
                        c = null;
                else
                    c = null;
                if (e = c)
                    if (c = 1E3 * e.start,
                    e = 1E3 * e.end,
                    0 == c && (c++,
                    e++),
                    !(e < c)) {
                        var f = {
                            id: d,
                            namespace: "annotations_module"
                        };
                        "marker" == b.type && (f.style = "ytp-chapter-marker",
                        f.tooltip = b.A,
                        f.visible = !0);
                        b = new g.jQ(c,e,f);
                        a.push(b)
                    }
            }
            g.cV(this.g, a)
        }
    }
    ;
    g.h.sD = function(a) {
        a == this.g.getVideoData().videoId && (this.loaded ? zya(this) : this.load())
    }
    ;
    g.h.KM = function(a) {
        a = a.getId();
        var b = this.o[a];
        b && !b.o && (b = b.annotation,
        Cya(this, a),
        F3(this.Ya, b.g))
    }
    ;
    g.h.LM = function(a) {
        h4(this, a.getId())
    }
    ;
    g.h.Mz = function(a) {
        a && (a.hide(),
        i4(this, "shown", !1, a.annotation.id),
        this.Xn(a.annotation, "hidden"))
    }
    ;
    g.h.LE = function(a) {
        a && (a.show(),
        i4(this, "shown", !0, a.annotation.id),
        this.Xn(a.annotation, "shown"))
    }
    ;
    g.h.GU = function(a, b, c) {
        var d = this.o[a];
        if (d && b.value != c) {
            b.value = c;
            var e = !1;
            gxa(d.annotation, function(a) {
                e = e || a.value
            });
            Dya(this, a, b, e)
        }
    }
    ;
    g.h.HQ = function(a) {
        if (a && a.id) {
            var b = a.Ta();
            if (b) {
                var c = a3(b);
                if (c) {
                    var d = (0,
                    g.z)(this.Xn, this, a, "click");
                    if ("new" == T3(c, b.target) || Eya(this, b))
                        d(),
                        d = null;
                    G3(this.Ya, a.g, d)
                }
            }
        }
    }
    ;
    g.h.Xn = function(a, b) {
        exa(a, function(c) {
            if (c.trigger == b && "openUrl" == c.type && c.url) {
                var d = this.g.getVideoData(), e;
                if (!(e = !Eya(this, c.url))) {
                    e = Xxa(c.url);
                    var f = Wxa(c.url);
                    e ? (d.videoId == e ? this.g.Zb(f || 0) : (d = f4(this).l,
                    g.F0(d.app, e, void 0, void 0, void 0, void 0, void 0),
                    f && f4(this).l.jd() && f4(this).l.Zb(f)),
                    d = !0) : d = !1;
                    e = !d
                }
                e && (e = g.Y(this.g),
                (d = a3(c.url)) ? (f = Vxa(d),
                e && "com" == f[0] && "google" == f[1] && "plus" == f[2] && (e = g.Ga(Yxa, e.pageId, e.Sc),
                d = new g.wm(d),
                g.Am(d, e(d.o)),
                d = d.toString())) : d = null,
                d && (this.g.kd(),
                c = T3(d, c.url.target),
                e = a.itct,
                "ei"in g.eE(d) && (d = g.Vg(d, "ei")),
                g.rV(d, "current" == c ? "_top" : void 0, {
                    itct: e
                })))
            }
        }, this)
    }
    ;
    g.h.dS = function() {
        i4(this, "playerControlShow", !1)
    }
    ;
    g.h.lU = function() {
        i4(this, "playerControlShow", !0)
    }
    ;
    g.h.MM = function(a) {
        i4(this, "rollOver", !0, a.id)
    }
    ;
    g.h.Ih = function(a) {
        i4(this, "rollOver", !1, a.id)
    }
    ;
    g.h.mR = function(a) {
        a && a.id && (this.o[a.id].o = !0,
        h4(this, a.id),
        H3(this.Ya, a.g),
        this.Xn(a, "close"),
        i4(this, "closed", !0, a.id))
    }
    ;
    g.h.Wl = function() {
        return !!this.l && this.l.isAvailable()
    }
    ;
    g.h.ZA = function() {
        this.Wl();
        return !!this.l && this.l.B
    }
    ;
    g.h.Xt = function(a, b, c) {
        b = void 0 === b ? !1 : b;
        this.Wl();
        this.l && (a ? c ? W3(this.l, c, b) : W3(this.l, "YOUTUBE_DRAWER_AUTO_OPEN", b) : X3(this.l))
    }
    ;
    g.h.Wt = function(a, b) {
        this.g.R(a ? "cardsteasershow" : "cardsteaserhide", b)
    }
    ;
    g.h.V = function() {
        g.Y(this.g).C.unsubscribe("vast_info_card_add", this.sD, this);
        g.mq(this.g.getRootNode(), "ytp-iv-drawer-open");
        for (var a = this.M, b = 0, c = a.length; b < c; b++)
            g.rG(a[b]);
        this.M.length = 0;
        g.oV.prototype.V.call(this)
    }
    ;
    var k4 = {}
      , Bza = "ontouchstart"in window.document;
    g.sF(window.document, "blur", l4, !0);
    g.sF(window.document, "change", l4, !0);
    g.sF(window.document, "click", l4);
    g.sF(window.document, "focus", l4, !0);
    g.sF(window.document, "mouseover", l4);
    g.sF(window.document, "mouseout", l4);
    g.sF(window.document, "mousedown", l4);
    g.sF(window.document, "keydown", l4);
    g.sF(window.document, "keyup", l4);
    g.sF(window.document, "keypress", l4);
    g.sF(window.document, "cut", l4);
    g.sF(window.document, "paste", l4);
    Bza && (g.sF(window.document, "touchstart", l4),
    g.sF(window.document, "touchend", l4),
    g.sF(window.document, "touchcancel", l4));
    g.h = m4.prototype;
    g.h.oe = function(a) {
        return g.$d(a, Z(this))
    }
    ;
    g.h.unregister = function() {
        g.WF(this.H);
        this.H.length = 0;
        g.LN(this.F);
        this.F.length = 0
    }
    ;
    g.h.init = g.y;
    g.h.dispose = g.y;
    g.h.addBehavior = function(a, b, c) {
        c = Z(this, c);
        var d = (0,
        g.z)(b, this);
        a in k4 || (k4[a] = new g.YC);
        k4[a].subscribe(c, d);
        this.D[b] = d
    }
    ;
    g.h.removeBehavior = function(a, b, c) {
        if (a in k4) {
            var d = k4[a];
            d.unsubscribe(Z(this, c), this.D[b]);
            0 >= d.Dc() && (d.dispose(),
            delete k4[a])
        }
        delete this.D[b]
    }
    ;
    g.h.qj = function(a, b, c) {
        var d = this.Ba(a, b);
        if (d && (d = g.x(d))) {
            var e = g.$a(arguments, 2);
            g.ab(e, 0, 0, a);
            d.apply(null, e)
        }
    }
    ;
    g.h.Ba = function(a, b) {
        return g.cF(a, b)
    }
    ;
    g.h.setData = function(a, b, c) {
        g.bF(a, b, c)
    }
    ;
    g.A(p4, m4);
    g.ya(p4);
    g.h = p4.prototype;
    g.h.register = function() {
        this.addBehavior("click", this.kG);
        this.addBehavior("keydown", this.vA);
        this.addBehavior("keypress", this.wA);
        n4(this, "page-scroll", this.tK)
    }
    ;
    g.h.unregister = function() {
        this.removeBehavior("click", this.kG);
        this.removeBehavior("keydown", this.vA);
        this.removeBehavior("keypress", this.wA);
        v4(this);
        this.l = {};
        p4.ba.unregister.call(this)
    }
    ;
    g.h.kG = function(a) {
        a && !a.disabled && (this.toggle(a),
        this.click(a))
    }
    ;
    g.h.vA = function(a, b, c) {
        if (!(c.altKey || c.ctrlKey || c.shiftKey || c.metaKey) && (b = u4(this, a))) {
            var d = function(a) {
                var b = "";
                a.tagName && (b = a.tagName.toLowerCase());
                return "ul" == b || "table" == b
            }, e;
            d(b) ? e = b : e = Z1(b, d);
            if (e) {
                e = e.tagName.toLowerCase();
                if ("ul" == e)
                    var f = this.uM;
                else
                    "table" == e && (f = this.tM);
                f && Gya(this, a, b, c, (0,
                g.z)(f, this))
            }
        }
    }
    ;
    g.h.tK = function() {
        var a = this.l;
        if (0 != g.Pb(a))
            for (var b in a) {
                var c = a[b]
                  , d = g.$d(c.activeButtonNode || c.parentNode, Z(this));
                if (void 0 == d || void 0 == c)
                    break;
                t4(this, d, c, !0)
            }
    }
    ;
    g.h.wA = function(a, b, c) {
        c.altKey || c.ctrlKey || c.shiftKey || c.metaKey || (a = u4(this, a),
        q2(a) && c.preventDefault())
    }
    ;
    g.h.tM = function(a, b, c) {
        var d = q4(this, b);
        if (d) {
            b = Xva("table", b);
            var e = Xva("tr", b);
            e = g.qd(window.document, "td", null, e).length;
            b = g.qd(window.document, "td", null, b);
            d = Iya(d, b, e, c);
            -1 != d && (Hya(this, a, b[d]),
            c.preventDefault())
        }
    }
    ;
    g.h.uM = function(a, b, c) {
        if (40 == c.keyCode || 38 == c.keyCode) {
            var d = q4(this, b);
            d && (b = (0,
            g.Ld)(g.qd(window.document, "li", null, b), q2),
            d = Iya(d, b, 1, c),
            Hya(this, a, b[d]),
            c.preventDefault())
        }
    }
    ;
    g.h.mG = function(a) {
        if (a) {
            var b = u4(this, a);
            if (b) {
                a.setAttribute("aria-pressed", "true");
                a.setAttribute("aria-expanded", "true");
                b.originalParentNode = b.parentNode;
                b.activeButtonNode = a;
                b.parentNode.removeChild(b);
                var c;
                this.Ba(a, "button-has-sibling-menu") ? c = a.parentNode : c = Jya(this, a);
                c.appendChild(b);
                b.style.minWidth = a.offsetWidth - 2 + "px";
                var d = s4(this, a);
                d && c.appendChild(d);
                (c = !!this.Ba(a, "button-menu-fixed")) && (this.l[m2(a).toString()] = b);
                t4(this, a, b, c);
                g.ZF("yt-uix-button-menu-before-show", a, b);
                r2(b);
                d && r2(d);
                this.qj(a, "button-menu-action", !0);
                g.S(a, Z(this, "active"));
                b = (0,
                g.z)(this.lG, this, a, !1);
                d = (0,
                g.z)(this.lG, this, a, !0);
                c = (0,
                g.z)(this.JW, this, a, void 0);
                this.g && u4(this, this.g) == u4(this, a) || v4(this);
                g.YF("yt-uix-button-menu-show", a);
                g.tF(this.o);
                this.o = [g.sF(window.document, "click", d), g.sF(window.document, "contextmenu", b), g.sF(window, "resize", c)];
                this.g = a
            }
        }
    }
    ;
    g.h.JW = function(a, b) {
        var c = u4(this, a);
        if (c) {
            b && (b instanceof g.Uc ? c.innerHTML = g.Vc(b) : g.Sd(c, b));
            var d = !!this.Ba(a, "button-menu-fixed");
            t4(this, a, c, d)
        }
    }
    ;
    g.h.dd = function() {
        return g.J(Z(this, "content"), void 0)
    }
    ;
    g.h.lG = function(a, b, c) {
        c = g.uF(c);
        var d = g.$d(c, Z(this));
        if (d) {
            d = u4(this, d);
            var e = u4(this, a);
            if (d == e)
                return
        }
        d = g.$d(c, Z(this, "menu"));
        e = d == u4(this, a);
        var f = g.kq(c, Z(this, "menu-item"))
          , k = g.kq(c, Z(this, "menu-close"));
        if (!d || e && (f || k))
            r4(this, a),
            d && b && this.Ba(a, "button-menu-indicate-selected") && ((a = g.J(Z(this, "content"), a)) && g.Sd(a, $1(c)),
            Kya(this, d, c))
    }
    ;
    g.h.isToggled = function(a) {
        return g.kq(a, Z(this, "toggled"))
    }
    ;
    g.h.toggle = function(a) {
        if (this.Ba(a, "button-toggle")) {
            var b = g.$d(a, Z(this, "group"))
              , c = Z(this, "toggled")
              , d = g.kq(a, c);
            if (b && this.Ba(b, "button-toggle-group")) {
                var e = this.Ba(b, "button-toggle-group");
                b = g.rd(Z(this), b);
                (0,
                g.B)(b, function(b) {
                    b != a || "optional" == e && d ? (g.mq(b, c),
                    b.removeAttribute("aria-pressed")) : (g.S(a, c),
                    b.setAttribute("aria-pressed", "true"))
                })
            } else
                d ? a.removeAttribute("aria-pressed") : a.setAttribute("aria-pressed", "true"),
                g.pq(a, c)
        }
    }
    ;
    g.h.click = function(a) {
        if (u4(this, a)) {
            var b = u4(this, a);
            if (b) {
                var c = g.$d(b.activeButtonNode || b.parentNode, Z(this));
                c && c != a ? (r4(this, c),
                g.YD((0,
                g.z)(this.mG, this, a), 1)) : q2(b) ? r4(this, a) : this.mG(a)
            }
            a.focus()
        }
        this.qj(a, "button-action")
    }
    ;
    g.A(w4, m4);
    g.h = w4.prototype;
    g.h.oe = function(a) {
        var b = m4.prototype.oe.call(this, a);
        return b ? b : a
    }
    ;
    g.h.register = function() {
        n4(this, "yt-uix-kbd-nav-move-out-done", this.hide)
    }
    ;
    g.h.dispose = function() {
        x4(this);
        w4.ba.dispose.call(this)
    }
    ;
    g.h.Ba = function(a, b) {
        var c = w4.ba.Ba.call(this, a, b);
        return c ? c : (c = w4.ba.Ba.call(this, a, "card-config")) && (c = g.x(c)) && c[b] ? c[b] : null
    }
    ;
    g.h.show = function(a) {
        var b = this.oe(a);
        if (b) {
            g.S(b, Z(this, "active"));
            var c = Lya(this, a, b);
            if (c) {
                c.cardTargetNode = a;
                c.cardRootNode = b;
                Mya(this, a, c);
                var d = Z(this, "card-visible")
                  , e = this.Ba(a, "card-delegate-show") && this.Ba(b, "card-action");
                this.qj(b, "card-action", a);
                this.o = a;
                s2(c);
                g.YD((0,
                g.z)(function() {
                    e || (r2(c),
                    g.YF("yt-uix-card-show", b, a, c));
                    Nya(c);
                    g.S(c, d);
                    g.YF("yt-uix-kbd-nav-move-in-to", c)
                }, this), 10)
            }
        }
    }
    ;
    g.h.hide = function(a) {
        if (a = this.oe(a)) {
            var b = g.pd(Z(this, "card") + m2(a));
            b && (g.mq(a, Z(this, "active")),
            g.mq(b, Z(this, "card-visible")),
            s2(b),
            this.o = null,
            b.cardTargetNode = null,
            b.cardRootNode = null,
            b.cardMask && (g.Kd(b.cardMask),
            b.cardMask = null))
        }
    }
    ;
    g.h.HW = function(a, b) {
        var c = this.oe(a);
        if (c) {
            if (b) {
                var d = this.Cc(c);
                if (!d)
                    return;
                b instanceof g.Uc ? d.innerHTML = g.Vc(b) : g.Sd(d, b)
            }
            g.kq(c, Z(this, "active")) && (c = Lya(this, a, c),
            Mya(this, a, c),
            r2(c),
            Nya(c))
        }
    }
    ;
    g.h.isActive = function(a) {
        return (a = this.oe(a)) ? g.kq(a, Z(this, "active")) : !1
    }
    ;
    g.h.Cc = function(a) {
        var b = a.cardContentNode;
        if (!b) {
            var c = Z(this, "content")
              , d = Z(this, "card-content");
            (b = (b = this.Ba(a, "card-id")) ? g.pd(b) : g.J(c, a)) || (b = window.document.createElement("div"));
            var e = b;
            g.mq(e, c);
            g.S(e, d);
            a.cardContentNode = b
        }
        return b
    }
    ;
    var z4;
    g.A(y4, m4);
    g.ya(y4);
    g.h = y4.prototype;
    g.h.register = function() {
        this.addBehavior("keydown", this.Fz);
        n4(this, "yt-uix-kbd-nav-move-in", this.mC);
        n4(this, "yt-uix-kbd-nav-move-in-to", this.nQ);
        n4(this, "yt-uix-kbd-move-next", this.nC);
        n4(this, "yt-uix-kbd-nav-move-to", this.jo)
    }
    ;
    g.h.unregister = function() {
        this.removeBehavior("keydown", this.Fz);
        g.tF(z4)
    }
    ;
    g.h.Fz = function(a, b, c) {
        var d = c.keyCode;
        if (a = g.$d(a, Z(this)))
            switch (d) {
            case 13:
            case 32:
                this.mC(a);
                break;
            case 27:
                c.preventDefault();
                c.stopImmediatePropagation();
                a: {
                    for (c = c2(a, "kbdNavMoveOut"); !c; ) {
                        c = g.$d(a.parentElement, Z(this));
                        if (!c)
                            break a;
                        c = c2(c, "kbdNavMoveOut")
                    }
                    c = g.pd(c);
                    this.jo(c);
                    g.YF("yt-uix-kbd-nav-move-out-done", c)
                }
                break;
            case 40:
            case 38:
                if ((b = c.target) && g.kq(a, Z(this, "list")))
                    switch (d) {
                    case 40:
                        this.nC(b, a);
                        break;
                    case 38:
                        d = window.document.activeElement == a,
                        a = Qya(a),
                        b = a.indexOf(b),
                        0 > b && !d || (b = d ? a.length - 1 : (a.length + b - 1) % a.length,
                        a[b].focus(),
                        Pya(this, a[b]))
                    }
                c.preventDefault()
            }
    }
    ;
    g.h.mC = function(a) {
        var b = c2(a, "kbdNavMoveIn");
        b = g.pd(b);
        Oya(this, a, b);
        this.jo(b)
    }
    ;
    g.h.nQ = function(a) {
        Oya(this, g.ae(), a);
        this.jo(a)
    }
    ;
    g.h.jo = function(a) {
        if (a)
            if (g.Xd(a))
                a.focus();
            else {
                var b = Z1(a, function(a) {
                    return g.Da(a) && 1 == a.nodeType ? g.Xd(a) : !1
                });
                b ? b.focus() : (a.setAttribute("tabindex", "-1"),
                a.focus())
            }
    }
    ;
    g.h.nC = function(a, b) {
        var c = window.document.activeElement == b
          , d = Qya(b)
          , e = d.indexOf(a);
        0 > e && !c || (c = c ? 0 : (e + 1) % d.length,
        d[c].focus(),
        Pya(this, d[c]))
    }
    ;
    g.A(A4, m4);
    g.ya(A4);
    g.h = A4.prototype;
    g.h.register = function() {
        this.addBehavior("click", this.nG);
        this.addBehavior("mouseenter", this.oK);
        n4(this, "page-scroll", this.DK);
        n4(this, "yt-uix-kbd-nav-move-out-done", function(a) {
            a = this.oe(a);
            E4(this, a)
        });
        this.A = new g.YC
    }
    ;
    g.h.unregister = function() {
        this.removeBehavior("click", this.nG);
        this.l = this.g = null;
        g.tF(Eva(g.Rb(this.o)));
        this.o = {};
        g.Lb(this.C, function(a) {
            g.Kd(a)
        }, this);
        this.C = {};
        g.$e(this.A);
        this.A = null;
        A4.ba.unregister.call(this)
    }
    ;
    g.h.nG = function(a, b, c) {
        a && (b = H4(this, a),
        !b.disabled && n2(c.target, b) && Tya(this, a))
    }
    ;
    g.h.oK = function(a, b, c) {
        a && g.kq(a, Z(this, "hover")) && (b = H4(this, a),
        n2(c.target, b) && Tya(this, a, !0))
    }
    ;
    g.h.DK = function() {
        this.g && this.l && Rya(this, this.l, this.g)
    }
    ;
    g.h.oG = function(a) {
        if (a) {
            var b = G4(this, a);
            if (b) {
                g.ZF("yt-uix-menu-before-show", a, b);
                if (this.g)
                    n2(a, this.g) || E4(this, this.l);
                else {
                    this.l = a;
                    this.g = b;
                    g.kq(a, Z(this, "sibling-content")) || (g.Kd(b),
                    window.document.body.appendChild(b));
                    var c = H4(this, a).offsetWidth - 2;
                    b.style.minWidth = c + "px"
                }
                (c = C4(this, a)) && b.parentNode && b.parentNode.insertBefore(c, b.nextSibling);
                g.mq(b, Z(this, "content-hidden"));
                Rya(this, a, b);
                g.lq(H4(this, a), [Z(this, "trigger-selected"), "yt-uix-button-toggled"]);
                g.YF("yt-uix-menu-show", a);
                Wya(b);
                Uya(this, a);
                g.YF("yt-uix-kbd-nav-move-in-to", b);
                var d = (0,
                g.z)(this.IX, this, a)
                  , e = (0,
                g.z)(this.qM, this, a);
                c = g.Fa(a).toString();
                this.o[c] = [g.sF(b, "click", e), g.sF(window.document, "click", d)];
                g.kq(a, Z(this, "indicate-selected")) && (d = (0,
                g.z)(this.rM, this, a),
                this.o[c].push(g.sF(b, "click", d)));
                g.kq(a, Z(this, "hover")) && (a = (0,
                g.z)(this.HX, this, a),
                this.o[c].push(g.sF(window.document, "mousemove", a)))
            }
        }
    }
    ;
    g.h.HX = function(a, b) {
        var c = g.uF(b);
        if (c) {
            var d = H4(this, a);
            n2(c, d) || Xya(this, c) || F4(this, a)
        }
    }
    ;
    g.h.IX = function(a, b) {
        var c = g.uF(b);
        if (c) {
            if (Xya(this, c)) {
                var d = g.$d(c, Z(this, "content"))
                  , e = g.Zd(c, "LI");
                e && d && g.Qd(d, e) && g.ZF("yt-uix-menu-item-clicked", c);
                c = g.$d(c, Z(this, "close-on-select"));
                if (!c)
                    return;
                d = B4(c)
            }
            E4(this, d || a)
        }
    }
    ;
    g.h.qM = function(a, b) {
        var c = g.uF(b);
        c && Vya(this, a, c)
    }
    ;
    g.h.rM = function(a, b) {
        var c = g.uF(b);
        if (c) {
            var d = H4(this, a);
            if (d && (c = g.Zd(c, "LI")))
                if (c = $1(c).trim(),
                d.hasChildNodes()) {
                    var e = p4.getInstance();
                    (d = g.J(Z(e, "content"), d)) && g.Sd(d, c)
                } else
                    g.Sd(d, c)
        }
    }
    ;
    g.A(I4, w4);
    g.ya(I4);
    g.h = I4.prototype;
    g.h.register = function() {
        I4.ba.register.call(this);
        this.addBehavior("click", this.fy, "target");
        this.addBehavior("click", this.ey, "close")
    }
    ;
    g.h.unregister = function() {
        I4.ba.unregister.call(this);
        this.removeBehavior("click", this.fy, "target");
        this.removeBehavior("click", this.ey, "close");
        for (var a in this.g)
            g.tF(this.g[a]);
        this.g = {};
        for (a in this.l)
            g.tF(this.l[a]);
        this.l = {}
    }
    ;
    g.h.fy = function(a, b, c) {
        c.preventDefault();
        b = g.Zd(c.target, "button");
        if (!b || !b.disabled) {
            b = this.Ba(a, "card-target");
            var d;
            b ? d = g.u(b) ? window.document.getElementById(b) : b : d = a;
            a = d;
            d = this.oe(a);
            this.Ba(d, "disabled") || (g.kq(d, Z(this, "active")) ? (this.hide(a),
            g.mq(d, Z(this, "active"))) : (this.show(a),
            g.S(d, Z(this, "active"))))
        }
    }
    ;
    g.h.show = function(a) {
        I4.ba.show.call(this, a);
        var b = this.oe(a)
          , c = g.Fa(a).toString();
        if (!g.cF(b, "click-outside-persists")) {
            if (this.g[c])
                return;
            b = g.sF(window.document, "click", (0,
            g.z)(this.gy, this, a));
            var d = g.sF(window, "blur", (0,
            g.z)(this.gy, this, a));
            this.g[c] = [b, d]
        }
        a = g.sF(window, "resize", (0,
        g.z)(this.HW, this, a, void 0));
        this.l[c] = a
    }
    ;
    g.h.hide = function(a) {
        I4.ba.hide.call(this, a);
        a = g.Fa(a).toString();
        var b = this.g[a];
        b && (g.tF(b),
        this.g[a] = null);
        if (b = this.l[a])
            g.tF(b),
            delete this.l[a]
    }
    ;
    g.h.gy = function(a, b) {
        var c = "yt-uix" + (this.B ? "-" + this.B : "") + "-card"
          , d = null;
        b.target && (d = g.$d(b.target, c) || g.$d(B4(b.target), c));
        (d = d || g.$d(window.document.activeElement, c) || g.$d(B4(window.document.activeElement), c)) || this.hide(a)
    }
    ;
    g.h.ey = function(a) {
        (a = g.$d(a, Z(this, "card"))) && (a = a.cardTargetNode) && this.hide(a)
    }
    ;
    g.A(J4, w4);
    g.ya(J4);
    g.h = J4.prototype;
    g.h.register = function() {
        this.addBehavior("mouseenter", this.iC, "target");
        this.addBehavior("mouseleave", this.kC, "target");
        this.addBehavior("mouseenter", this.jC, "card");
        this.addBehavior("mouseleave", this.lC, "card")
    }
    ;
    g.h.unregister = function() {
        this.removeBehavior("mouseenter", this.iC, "target");
        this.removeBehavior("mouseleave", this.kC, "target");
        this.removeBehavior("mouseenter", this.jC, "card");
        this.removeBehavior("mouseleave", this.lC, "card")
    }
    ;
    g.h.iC = function(a) {
        if (W4 != a) {
            W4 && (this.hide(W4),
            W4 = null);
            var b = (0,
            g.z)(this.show, this, a)
              , c = (0,
            window.parseInt)(this.Ba(a, "delay-show"), 10);
            b = g.YD(b, -1 < c ? c : 200);
            this.setData(a, "card-timer", b.toString());
            W4 = a;
            a.alt && (this.setData(a, "card-alt", a.alt),
            a.alt = "");
            a.title && (this.setData(a, "card-title", a.title),
            a.title = "")
        }
    }
    ;
    g.h.kC = function(a) {
        var b = (0,
        window.parseInt)(this.Ba(a, "card-timer"), 10);
        g.$D(b);
        this.oe(a).isCardHidable = !0;
        b = (0,
        window.parseInt)(this.Ba(a, "delay-hide"), 10);
        b = -1 < b ? b : 200;
        g.YD((0,
        g.z)(this.WK, this, a), b);
        if (b = this.Ba(a, "card-alt"))
            a.alt = b;
        if (b = this.Ba(a, "card-title"))
            a.title = b
    }
    ;
    g.h.WK = function(a) {
        this.oe(a).isCardHidable && (this.hide(a),
        W4 = null)
    }
    ;
    g.h.jC = function(a) {
        a && (a.cardRootNode.isCardHidable = !1)
    }
    ;
    g.h.lC = function(a) {
        a && this.hide(a.cardTargetNode)
    }
    ;
    var W4 = null;
    var $ya = {
        LOADING: "loading",
        OG: "content",
        T4: "working"
    };
    g.h = K4.prototype;
    g.h.show = function() {
        if (!this.ka()) {
            this.D = window.document.activeElement;
            if (!this.M) {
                this.l || (this.l = g.pd("yt-dialog-bg"),
                this.l || (this.l = g.Ed("div"),
                this.l.id = "yt-dialog-bg",
                this.l.className = "yt-dialog-bg",
                window.document.body.appendChild(this.l)));
                var a = window
                  , b = a.document;
                var c = 0;
                if (b) {
                    c = b.body;
                    var d = b.documentElement;
                    if (d && c)
                        if (a = g.wd(a).height,
                        g.ud(b) && d.scrollHeight)
                            c = d.scrollHeight != a ? d.scrollHeight : d.offsetHeight;
                        else {
                            b = d.scrollHeight;
                            var e = d.offsetHeight;
                            d.clientHeight != e && (b = c.scrollHeight,
                            e = c.offsetHeight);
                            c = b > a ? b > e ? b : e : b < e ? b : e
                        }
                    else
                        c = 0
                }
                this.l.style.height = c + "px";
                r2(this.l)
            }
            this.Nz();
            c = bza(this);
            cza(c);
            this.B = g.sF(window.document, "keydown", (0,
            g.z)(this.aM, this));
            c = this.g;
            d = g.VF("player-added", this.Nz, this);
            g.bF(c, "player-ready-pubsub-key", d);
            this.T && (this.C = g.sF(window.document, "click", (0,
            g.z)(this.lV, this)));
            r2(this.g);
            this.o.setAttribute("tabindex", "0");
            eza(this);
            this.H || g.S(window.document.body, "yt-dialog-active");
            v4(p4.getInstance());
            x4(I4.getInstance());
            x4(J4.getInstance());
            g.YF("yt-ui-dialog-show-complete", this)
        }
    }
    ;
    g.h.Nz = function() {
        if (!this.X) {
            var a = this.g;
            g.U(window.document.body, "hide-players", !0);
            a && g.U(a, "preserve-players", !0)
        }
    }
    ;
    g.h.jR = function(a) {
        a = a.currentTarget;
        a.disabled || (a = g.cF(a, "action") || "",
        this.dismiss(a))
    }
    ;
    g.h.dismiss = function(a) {
        if (!this.ka()) {
            this.A.R("pre-all");
            this.A.R("pre-" + a);
            s2(this.g);
            x4(I4.getInstance());
            x4(J4.getInstance());
            this.o.setAttribute("tabindex", "-1");
            aza() || (s2(this.l),
            this.H || g.mq(window.document.body, "yt-dialog-active"),
            Yva(),
            dza());
            this.B && (g.tF(this.B),
            this.B = null);
            this.C && (g.tF(this.C),
            this.C = null);
            var b = this.g;
            if (b) {
                var c = g.cF(b, "player-ready-pubsub-key");
                c && (g.WF(c),
                l2(b, "player-ready-pubsub-key"))
            }
            this.A.R("post-all");
            g.YF("yt-ui-dialog-hide-complete", this);
            "cancel" == a && g.YF("yt-ui-dialog-cancelled", this);
            this.A && this.A.R("post-" + a);
            this.D && this.D.focus()
        }
    }
    ;
    g.h.setTitle = function(a) {
        g.Sd(g.J("yt-dialog-title", this.g), a)
    }
    ;
    g.h.aM = function(a) {
        g.YD((0,
        g.z)(function() {
            this.K || 27 != a.keyCode || this.dismiss("cancel")
        }, this), 0);
        9 == a.keyCode && a.shiftKey && g.kq(window.document.activeElement, "yt-dialog-fg") && a.preventDefault()
    }
    ;
    g.h.lV = function(a) {
        "yt-dialog-base" == a.target.className && this.dismiss("cancel")
    }
    ;
    g.h.ka = function() {
        return this.J
    }
    ;
    g.h.dispose = function() {
        q2(this.g) && this.dismiss("dispose");
        g.tF(this.F);
        this.F.length = 0;
        g.YD((0,
        g.z)(function() {
            this.D = null
        }, this), 0);
        this.I = this.o = null;
        this.A.dispose();
        this.A = null;
        this.J = !0
    }
    ;
    g.h.NJ = function(a) {
        a.stopPropagation();
        eza(this)
    }
    ;
    g.va("yt.ui.Dialog", K4, void 0);
    g.A(L4, m4);
    g.ya(L4);
    g.h = L4.prototype;
    g.h.register = function() {
        this.addBehavior("click", this.Cv, "target");
        this.addBehavior("click", this.hide, "close");
        gza(this)
    }
    ;
    g.h.unregister = function() {
        L4.ba.unregister.call(this);
        this.removeBehavior("click", this.Cv, "target");
        this.removeBehavior("click", this.hide, "close");
        this.A && (g.WF(this.A),
        this.A = null);
        this.l && (g.tF(this.l),
        this.l = null)
    }
    ;
    g.h.Cv = function(a) {
        if (!this.g || !q2(this.g.g)) {
            var b = this.oe(a);
            a = iza(b, a);
            b || (b = a ? a.overlayParentNode : null);
            if (b && a) {
                var c = !!this.Ba(b, "disable-shortcuts") || !1
                  , d = !!this.Ba(b, "disable-outside-click-dismiss") || !1;
                this.g = new K4(a,c);
                this.o = b;
                var e = g.J("yt-dialog-fg", a);
                if (e) {
                    var f = this.Ba(b, "overlay-class") || ""
                      , k = this.Ba(b, "overlay-style") || "default"
                      , l = this.Ba(b, "overlay-shape") || "default";
                    f = f ? f.split(" ") : [];
                    f.push(Z(this, k));
                    f.push(Z(this, l));
                    g.lq(e, f)
                }
                this.g.show();
                g.YF("yt-uix-kbd-nav-move-to", e || a);
                gza(this);
                c || d || (c = (0,
                g.z)(function(a) {
                    g.kq(a.target, "yt-dialog-base") && hza(this)
                }, this),
                a = g.J("yt-dialog-base", a),
                this.l = g.sF(a, "click", c));
                this.qj(b, "overlay-shown");
                g.YF("yt-uix-overlay-shown", b)
            }
        }
    }
    ;
    g.h.Cc = function(a) {
        return g.J("yt-dialog-content", a.overlayContentNode || a)
    }
    ;
    g.h.hide = function(a) {
        a && a.disabled || g.YF("yt-uix-overlay-hide")
    }
    ;
    g.h.show = function(a) {
        this.Cv(a)
    }
    ;
    g.A(M4, m4);
    g.ya(M4);
    g.h = M4.prototype;
    g.h.register = function() {
        this.addBehavior("mouseover", this.yp);
        this.addBehavior("mouseout", this.Pi);
        this.addBehavior("focus", this.Zy);
        this.addBehavior("blur", this.Mx);
        this.addBehavior("click", this.Pi);
        this.addBehavior("touchstart", this.cF);
        this.addBehavior("touchend", this.rq);
        this.addBehavior("touchcancel", this.rq)
    }
    ;
    g.h.unregister = function() {
        this.removeBehavior("mouseover", this.yp);
        this.removeBehavior("mouseout", this.Pi);
        this.removeBehavior("focus", this.Zy);
        this.removeBehavior("blur", this.Mx);
        this.removeBehavior("click", this.Pi);
        this.removeBehavior("touchstart", this.cF);
        this.removeBehavior("touchend", this.rq);
        this.removeBehavior("touchcancel", this.rq);
        this.dispose();
        M4.ba.unregister.call(this)
    }
    ;
    g.h.dispose = function() {
        for (var a in this.l)
            this.Pi(this.l[a]);
        this.l = {}
    }
    ;
    g.h.yp = function(a) {
        if (!(this.g && 1E3 > (0,
        g.F)() - this.g)) {
            var b = (0,
            window.parseInt)(this.Ba(a, "tooltip-hide-timer"), 10);
            b && (l2(a, "tooltip-hide-timer"),
            g.$D(b));
            b = (0,
            g.z)(function() {
                oza(this, a);
                l2(a, "tooltip-show-timer")
            }, this);
            var c = (0,
            window.parseInt)(this.Ba(a, "tooltip-show-delay"), 10) || 0;
            b = g.YD(b, c);
            this.setData(a, "tooltip-show-timer", b.toString());
            a.title && (this.setData(a, "tooltip-text", lza(this, a)),
            a.title = "");
            b = g.Fa(a).toString();
            this.l[b] = a
        }
    }
    ;
    g.h.Pi = function(a) {
        var b = (0,
        window.parseInt)(this.Ba(a, "tooltip-show-timer"), 10);
        b && (g.$D(b),
        l2(a, "tooltip-show-timer"));
        b = (0,
        g.z)(function() {
            if (a) {
                var b = g.pd(N4(this, a));
                b && (pza(b),
                g.Kd(b),
                l2(a, "content-id"));
                b = g.pd(N4(this, a, "arialabel"));
                g.Kd(b)
            }
            l2(a, "tooltip-hide-timer")
        }, this);
        b = g.YD(b, 50);
        this.setData(a, "tooltip-hide-timer", b.toString());
        if (b = this.Ba(a, "tooltip-text"))
            a.title = b;
        b = g.Fa(a).toString();
        delete this.l[b]
    }
    ;
    g.h.Zy = function(a, b) {
        this.g = 0;
        this.yp(a, b)
    }
    ;
    g.h.Mx = function(a) {
        this.g = 0;
        this.Pi(a)
    }
    ;
    g.h.cF = function(a, b, c) {
        c.changedTouches && (this.g = 0,
        (a = j4(b, Z(this), c.changedTouches[0].target)) && this.yp(a, b))
    }
    ;
    g.h.rq = function(a, b, c) {
        c.changedTouches && (this.g = (0,
        g.F)(),
        (a = j4(b, Z(this), c.changedTouches[0].target)) && this.Pi(a))
    }
    ;
    var X4 = window.yt && window.yt.uix && window.yt.uix.widgets_ || {};
    g.va("yt.uix.widgets_", X4, void 0);
    g.A(O4, g.CN);
    g.A(P4, g.CN);
    g.A(qza, g.CN);
    g.A(Q4, g.CN);
    var Cza = new g.DN("subscription-subscribe",P4)
      , Dza = new g.DN("subscription-subscribe-loading",O4)
      , Eza = new g.DN("subscription-subscribe-loaded",O4)
      , Fza = new g.DN("subscription-subscribe-success",qza)
      , Gza = new g.DN("subscription-unsubscribe",Q4)
      , Hza = new g.DN("subscription-unsubscirbe-loading",O4)
      , Iza = new g.DN("subscription-unsubscribe-loaded",O4)
      , Jza = new g.DN("subscription-unsubscribe-success",O4)
      , Kza = new g.DN("subscription-enable-ypc",O4)
      , Lza = new g.DN("subscription-disable-ypc",O4);
    var S4 = {}
      , R4 = [];
    g.A(T4, m4);
    g.ya(T4);
    T4.prototype.register = function() {
        this.addBehavior("click", this.pw);
        o4(this, Dza, this.UC);
        o4(this, Eza, this.pG);
        o4(this, Fza, this.uU);
        o4(this, Hza, this.UC);
        o4(this, Iza, this.pG);
        o4(this, Jza, this.HU);
        o4(this, Kza, this.MR);
        o4(this, Lza, this.GR)
    }
    ;
    T4.prototype.unregister = function() {
        this.removeBehavior("click", this.pw);
        T4.ba.unregister.call(this)
    }
    ;
    T4.prototype.isSubscribed = function(a) {
        return !!this.Ba(a, "is-subscribed")
    }
    ;
    var V4 = {
        uw: "hover-enabled",
        EG: "yt-uix-button-subscribe",
        FG: "yt-uix-button-subscribed",
        MY: "ypc-enabled",
        MG: "yt-uix-button-subscription-container",
        NG: "yt-subscription-button-disabled-mask-container"
    }
      , U4 = {
        dZ: "channel-external-id",
        QG: "subscriber-count-show-when-subscribed",
        SG: "subscriber-count-tooltip",
        TG: "subscriber-count-title",
        P_: "href",
        Y_: "insecure",
        Iw: "is-subscribed",
        O1: "parent-url",
        Q2: "clicktracking",
        kI: "show-unsub-confirm-dialog",
        T2: "show-unsub-confirm-time-frame",
        sI: "style-type",
        jx: "subscribed-timestamp",
        kx: "subscription-id",
        y3: "target",
        MI: "ypc-enabled"
    };
    g.h = T4.prototype;
    g.h.pw = function(a) {
        var b = this.Ba(a, "href")
          , c = this.Ba(a, "insecure");
        if (b)
            a = this.Ba(a, "target") || "_self",
            window.open(b, a);
        else if (!c)
            if (g.VV()) {
                b = this.Ba(a, "channel-external-id");
                c = this.Ba(a, "clicktracking");
                var d = tza(this, a)
                  , e = this.Ba(a, "parent-url");
                if (this.Ba(a, "is-subscribed")) {
                    var f = this.Ba(a, "subscription-id")
                      , k = new Q4(b,f,d,a,c,e);
                    xza(this, a) ? sza(a, b).then(function() {
                        g.FN(Gza, k)
                    }) : g.FN(Gza, k)
                } else
                    g.FN(Cza, new P4(b,d,c,e))
            } else
                wza(this, a)
    }
    ;
    g.h.UC = function(a) {
        this.nj(a.g, this.vE, !0)
    }
    ;
    g.h.pG = function(a) {
        this.nj(a.g, this.vE, !1)
    }
    ;
    g.h.uU = function(a) {
        this.nj(a.g, this.BE, !0, a.l)
    }
    ;
    g.h.HU = function(a) {
        this.nj(a.g, this.BE, !1)
    }
    ;
    g.h.MR = function(a) {
        this.nj(a.g, this.FJ)
    }
    ;
    g.h.GR = function(a) {
        this.nj(a.g, this.yJ)
    }
    ;
    g.h.BE = function(a, b, c) {
        b ? (this.setData(a, U4.Iw, "true"),
        c && this.setData(a, U4.kx, c),
        this.Ba(a, U4.kI) && (b = new d2,
        this.setData(a, U4.jx, (b.getTime() / 1E3).toString()))) : (l2(a, U4.Iw),
        l2(a, U4.jx),
        l2(a, U4.kx));
        uza(this, a)
    }
    ;
    g.h.vE = function(a, b) {
        var c = g.$d(a, V4.MG);
        g.U(c, V4.NG, b);
        a.setAttribute("aria-busy", b ? "true" : "false");
        a.disabled = b
    }
    ;
    g.h.FJ = function(a) {
        var b = !!this.Ba(a, "ypc-item-type")
          , c = !!this.Ba(a, "ypc-item-id");
        !this.Ba(a, "ypc-enabled") && b && c && (g.S(a, "ypc-enabled"),
        this.setData(a, U4.MI, "true"))
    }
    ;
    g.h.yJ = function(a) {
        this.Ba(a, "ypc-enabled") && (g.mq(a, "ypc-enabled"),
        l2(a, "ypc-enabled"))
    }
    ;
    g.h.cJ = function(a, b, c) {
        var d = g.$a(arguments, 2);
        (0,
        g.B)(a, function(a) {
            b.apply(this, g.Xa(a, d))
        }, this)
    }
    ;
    g.h.nj = function(a, b, c) {
        var d = vza(this, a);
        d = g.Xa([d], g.$a(arguments, 1));
        this.cJ.apply(this, d)
    }
    ;
    g.fY.annotations_module = e4;
    g.fY.creatorendscreen = K2;
    var Y4 = T4.getInstance()
      , Z4 = Z(Y4);
    Z4 in X4 || (Y4.register(),
    n4(Y4, "yt-uix-init-" + Z4, Y4.init),
    n4(Y4, "yt-uix-dispose-" + Z4, Y4.dispose),
    X4[Z4] = Y4);
}
)(_yt_player);
