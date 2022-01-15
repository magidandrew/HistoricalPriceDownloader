import yfinance as yf
import sys

args: list[str] = [x for x in sys.argv if x != sys.argv[0]]
yf_tickers = []

for ticker in args:
    yf_tickers.append(yf.Ticker(ticker))

for yf_