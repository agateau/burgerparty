#!/usr/bin/env python
# encoding: utf-8
import string
import sys
import argparse

DESCRIPTION = """\
Generate Signal$N classes from template
"""

def main():
    parser = argparse.ArgumentParser(description=DESCRIPTION)

    parser.add_argument("n", metavar="N", type=int, nargs=1,
        help="Generate classes for 0 to N arguments")

    args = parser.parse_args()

    tmpl_text = open("Signal.java.tmpl").read()
    tmpl = string.Template(tmpl_text)

    for n in range(args.n[0] + 1):
        out_name = "Signal%d.java" % n
        print "Generating %s..." % out_name
        out = open(out_name, "w")
        n_list = range(1, n + 1)
        args = dict(
            generator=parser.prog,
            n=n,
            typeList=", ".join(["T%d" % x for x in n_list]),
            typeArgList=", ".join(["T%d a%d" % (x, x) for x in n_list]),
            argList=", ".join(["a%d" % x for x in n_list]),
        )

        code = tmpl.substitute(args)
        if n == 0:
            # Hack: empty template signs are illegals
            code = code.replace("<>", "")
        out.write(code)

    return 0


if __name__ == "__main__":
    sys.exit(main())
# vi: ts=4 sw=4 et
