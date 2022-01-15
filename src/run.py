import sys
import yfinance as yf
import pandas as pd
from functools import reduce

OUTPUT_NAME = "report"

args: list[str] = [x for x in sys.argv if x != sys.argv[0]]

excel = False
csv = False

if "-e" in args:
    excel = True
    args.remove("-e")
elif "-c" in args:
    csv = True
    args.remove("-c")

yf_tickers: list[yf.Ticker] = [yf.Ticker(x) for x in args]
histories = [x.history(period="max", interval="1mo") for x in yf_tickers]
merged = reduce(lambda x, y: pd.merge(x['Close'].dropna(), y['Close'].dropna(), on='Date'), histories)
merged.columns = [x.ticker.__str__() for x in yf_tickers]
merged.index = pd.to_datetime(merged.index, format = '%m/%d/%Y').strftime('%Y-%m-%d')
if excel:
    merged.to_excel(f"{OUTPUT_NAME}.xlsx")
elif csv:
    merged.to_csv(f"{OUTPUT_NAME}.csv")
print(yf_tickers)