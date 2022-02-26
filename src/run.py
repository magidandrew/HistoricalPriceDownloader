import sys
import yfinance as yf
import pandas as pd
from functools import reduce

# usage: python3 run.py <output_filename> (-e,-c) [ticker1, ticker2...]

args: list[str] = [x for x in sys.argv if x != sys.argv[0]]

excel = False
csv = False

if "-e" in args:
    excel = True
    args.remove("-e")
elif "-c" in args:
    csv = True
    args.remove("-c")

OUTPUT_NAME = args[0]
del args[0]

yf_tickers: list[yf.Ticker] = [yf.Ticker(x) for x in args]
histories = [x.history(period="max", interval="1mo")[['Close']].dropna() for x in yf_tickers]

if len(yf_tickers) == 1:
    merged = histories[0][['Close']].dropna()
else:
    merged = reduce(lambda x, y: pd.merge(x, y, on='Date'), histories)

merged.columns = [x.ticker for x in yf_tickers]
merged.index = pd.to_datetime(merged.index, format = '%m/%d/%Y').strftime('%Y-%m-%d')
if excel:
    merged.to_excel(f"{OUTPUT_NAME}.xlsx")
elif csv:
    merged.to_csv(f"{OUTPUT_NAME}.csv")
