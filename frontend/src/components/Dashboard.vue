<!--  
***  2020.06.05 Now uses centralized functions for enforcing/restoring authourization
***  2020.06.13 Implemented display of dividend per instrument
***  2020.06.17 Implemented display dividend timeline chart
-->

<template>
  <div id="layoutSidenav_content">
    <main>
      <div class="container-fluid">
        <div class="row mt-3">
          <div class="col-lg-3">
            <h3 class="text-primary float-left">Dashboard</h3>
          </div>
        </div>
        <hr class="bg-primary" />
        <!-- The card row - could also use b-card-group and b-card from bootstrap-vue, this approach is just more flexible-->
        <div class="row">
          <div class="col-xl-3 col-md-6">
            <div class="card bg-primary text-white mb-4">
              <div class="card-header d-flex align-items-center justify-content-between">
                <a class="text-white stretched-link" href="#">Portfolio Value</a>
                <div class="small text-white">
                  <font-awesome-icon :icon="['fas', 'angle-right']" />
                </div>
              </div>
              <div class="card-body">
                <h1>{{portfolioValue}}</h1>
              </div>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bg-primary text-white mb-4">
              <div class="card-header d-flex align-items-center justify-content-between">
                <a class="text-white stretched-link" href="#">Total Dividends</a>
                <div class="small text-white">
                  <font-awesome-icon :icon="['fas', 'angle-right']" />
                </div>
              </div>
              <div class="card-body">
                <h1>{{dividends.sums.all}}</h1>
              </div>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bg-primary text-white mb-4">
              <div class="card-header d-flex align-items-center justify-content-between">
                <a class="text-white stretched-link" href="#">Dividends TTM</a>
                <div class="small text-white">
                  <font-awesome-icon :icon="['fas', 'angle-right']" />
                </div>
              </div>
              <div class="card-body">
                <h1>{{dividends.sums.ttm}}</h1>
              </div>
            </div>
          </div>
          <div class="col-xl-3 col-md-6">
            <div class="card bg-primary text-white mb-4">
              <div class="card-header d-flex align-items-center justify-content-between">
                <a class="text-white stretched-link" href="#">Dividends YTD</a>
                <div class="small text-white">
                  <font-awesome-icon :icon="['fas', 'angle-right']" />
                </div>
              </div>
              <div class="card-body">
                <h1>{{dividends.sums.ytd}}</h1>
              </div>
            </div>
          </div>
        </div>
        <!-- -->
        <div class="row ml-2">
          <div>
            <line-chart :chartData="lineChartData"></line-chart>
          </div>
          <div>
            <doughnut :chartData="doughnutChartData"></doughnut>
          </div>
        </div>

        <!-- -->
        <div class="chart-container mt-4 mb-4">
          <bar-chart :chartData="instrumentDividendsChartInfo.chartData" :options="instrumentDividendsChartInfo.chartOptions"></bar-chart>
        </div>

        <!-- -->

        <div class="chart-container mt-4 mb-4">
          <div>
            <line-chart :chartData="dividendTimelineChartInfo.chartData" :options="dividendTimelineChartInfo.chartOptions"></line-chart>
          </div>
        </div>

        <!-- -->
      </div>
    </main>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import LineChart from "../components/charts/LineChart";
import Doughnut from "../components/charts/Doughnut";
import BarChart from "../components/charts/BarChart";

//https://github.com/apertureless/vue-chartjs/issues/17
//https://www.chartjs.org/docs/latest/general/responsive.html

export default {
  name: "Dashboard",
  components: { LineChart, Doughnut, BarChart },
  computed: {
    ...mapState(["isBasicDataGotten", "authenticated", "transactions", "supportedInstruments", "fxr", "displayFormats", "formmaterIndex", "currencies"]),
    portfolioValue() {
      return 500000;
    },
    fx() {
      const baseCurrencyId = this.displayFormats[this.formmaterIndex].baseCurrencyId;
      const fx = {
        usd: this.fxr.find(item => item.fromId == 1001101 && item.toId == baseCurrencyId).converter,
        gbp: this.fxr.find(item => item.fromId == 1001103 && item.toId == baseCurrencyId).converter,
        eur: this.fxr.find(item => item.fromId == 1001105 && item.toId == baseCurrencyId).converter,
        usx: this.fxr.find(item => item.fromId == 1001102 && item.toId == baseCurrencyId).converter,
        gbx: this.fxr.find(item => item.fromId == 1001104 && item.toId == baseCurrencyId).converter,
        eux: this.fxr.find(item => item.fromId == 1001106 && item.toId == baseCurrencyId).converter
      };
      return fx;
    },
    instrumentDividendsChartInfo() {
      const labels = [];
      const data = [];
      let seriesLabel = null;

      if (this.dividends.instrumentAccummulations.length > 0) {
        const safeinstrumentAccummulations = this.dividends.instrumentAccummulations.slice();

        switch (this.accumulatedDividendsRange) {
          case 0:
            seriesLabel = "Dividend (YTD)";
            safeinstrumentAccummulations.sort(function(a, b) {
              return a.ytd - b.ytd;
            });
            for (const iDivValues of this.dividends.instrumentAccummulations) {
              if (iDivValues.ytd > 0) {
                labels.push(iDivValues.code);
                data.push(iDivValues.ytd);
              }
            }
            break;
          case 1:
            seriesLabel = "Dividend (TTM)";
            safeinstrumentAccummulations.sort(function(a, b) {
              return a.ttm - b.ttm;
            });
            for (const iDivValues of this.dividends.instrumentAccummulations) {
              if (iDivValues.ttm > 0) {
                labels.push(iDivValues.code);
                data.push(iDivValues.ttm);
              }
            }
            break;
          case 2:
            seriesLabel = "Dividend (5YRS)";
            safeinstrumentAccummulations.sort(function(a, b) {
              return a.l5y - b.l5y;
            });
            for (const iDivValues of this.dividends.instrumentAccummulations) {
              if (iDivValues.l5y > 0) {
                labels.push(iDivValues.code);
                data.push(iDivValues.l5y);
              }
            }
            break;
          default:
            seriesLabel = "Dividend (ALL)";
            safeinstrumentAccummulations.sort(function(a, b) {
              return a.all - b.all;
            });
            for (const iDivValues of safeinstrumentAccummulations) {
              if (iDivValues.all > 0) {
                labels.push(iDivValues.code);
                data.push(iDivValues.all);
              }
            }
        }
      }

      const options = {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          xAxes: [
            {
              gridLines: {
                drawOnChartArea: false
              },
              ticks: { fontSize: 16, fontStyle: "bold", fontColor: "#007bff" }
            }
          ],
          yAxes: [
            {
              gridLines: {
                drawOnChartArea: false
                //lineWidth: 1.0, color: 'rgba(0,123,255, 0.15)'
              },
              ticks: { fontSize: 16, fontStyle: "bold", fontColor: "#007bff" }
            }
          ]
        }
        // tooltips: { //https://stackoverflow.com/questions/25880767/chart-js-number-format
        //   callbacks: {
        //     label: function(tooltipItem) {
        //       return tooltipItem.yLabel.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, "$&,");
        //     }
        //   }
        // }
      };

      const result = {
        chartData: {
          labels: labels,
          datasets: [
            {
              label: seriesLabel,
              data: data,
              backgroundColor: "rgba(0,123,255, 1.0)",
              hoverBackgroundColor: "rgba(52,58,64, 0.5)"
            }
          ]
        },
        chartOptions: options
      };

      return result;
    },
    dividendTimelineChartInfo() {
      const datasets = [];
      console.log("Heading");

      if (this.dividends.dividendTimelineData.quarterly.dates.length > 0) {
        //let safeinstrumentAccummulations = this.dividends.instrumentAccummulations.slice();
        console.log("Heading2");
        let dataSource = null;
        let shift = 0;
        switch (this.timeIncrements) {
          case 0:
            dataSource = this.dividends.dividendTimelineData.monthly;
            shift = 15 * 24 * 60 * 60 * 1000; //millis
            break;
          case 1:
            dataSource = this.dividends.dividendTimelineData.quarterly;
            shift = 45 * 24 * 60 * 60 * 1000; //millis
            break;
          case 2:
            dataSource = this.dividends.dividendTimelineData.halfYearly;
            shift = 90 * 24 * 60 * 60 * 1000; //millis
            break;
          default:
            dataSource = this.dividends.dividendTimelineData.yearly;
            shift = 180 * 24 * 60 * 60 * 1000; //millis
        }

        const midPeriodDates = [];
        for (let i = 0; i < dataSource.dates.length; i++) {
          const dd = dataSource.dates[i];
          const dds = new Date(dd);
          dds.setTime(dd.getTime() + shift);
          midPeriodDates.push(dds);
        }

        datasets.push({ label: "EUR", data: this.getTimeSeriesDataset(midPeriodDates, dataSource.eur) });
        datasets.push({backgroundColor: 'rgba(0,123,255, 1.0)', label: "USD", data: this.getTimeSeriesDataset(midPeriodDates, dataSource.usd) });
        datasets.push({fill: '-1', backgroundColor: "rgba(220, 0, 0, 0.2)", label: "GBP", data: this.getTimeSeriesDataset(midPeriodDates, dataSource.gbp) });

        // //round about way of stacking - chart.js
        // const eurusd = dataSource.eur.map((e, i) => e + dataSource.usd[i]);
        // const eurusdgbp = dataSource.eur.map((e, i) => e + dataSource.usd[i] + dataSource.gbp[i]);
        // datasets.push({ backgroundColor: "rgba(0,123,255, 1.0)", label: "USD", data: this.getTimeSeriesDataset(midPeriodDates, eurusd) });
        // datasets.push({ fill: "-1", backgroundColor: "rgba(220, 0, 0, 0.2)", label: "GBP", data: this.getTimeSeriesDataset(midPeriodDates, eurusdgbp) });
      }
//www.alphavantage.co  Welcome to Alpha Vantage! Your API key is: 3XJV7GG70EKZJ7DN. Please record this API key for future access to Alpha Vantage.
      const options = {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          xAxes: [
            {
              gridLines: {
                drawOnChartArea: false
              },
              type: "time"
              //ticks: { fontSize: 16, fontStyle: "bold", fontColor: "#007bff" }
            }
          ],
          yAxes: [
            {
              gridLines: {
                drawOnChartArea: false
                //lineWidth: 1.0, color: 'rgba(0,123,255, 0.15)'
              },
              ticks: { fontSize: 16, fontStyle: "bold", fontColor: "#007bff" },
              stacked: true
            }
          ]
        }
      };

      return { chartData: { datasets: datasets }, chartOptions: options };
    },
    dividends() {
      const instrumentDivValues = [];
      const format = this.displayFormats[this.formmaterIndex];
      const formatter = new Intl.NumberFormat(format.locale, { style: "currency", currency: format.currency });

      const currentDate = new Date();

      const dividendTimelineData = {
        monthly: { dates: [] },
        quarterly: { dates: [] },
        halfYearly: { dates: [] },
        yearly: { dates: [] }
      };

      //fill dates
      const end = new Date(currentDate.getFullYear() + 1, 0, 1);
      this.prepareTimelineData(dividendTimelineData.monthly, 1, end);
      this.prepareTimelineData(dividendTimelineData.quarterly, 3, end);
      this.prepareTimelineData(dividendTimelineData.halfYearly, 6, end);
      this.prepareTimelineData(dividendTimelineData.yearly, 12, end);
      //

      /**
       * datasets: [
          { label: "USD", data: [20, 52, 17, 4] },
          { label: "GBP", data: [4, 1, 7, 2] }
        ]
       */

      //https://stackoverflow.com/a/35970005  this.supportedInstruments.foreach(instrument => {}); will not work
      for (const instrument of this.supportedInstruments) {
        instrumentDivValues.push({
          ytd: 0,
          ttm: 0,
          l5y: 0,
          all: 0,
          code: instrument.code
        });
      }

      const ytdDate = new Date(currentDate.getFullYear(), 0, 1);
      const ttmDate = new Date(currentDate.getFullYear() - 1, currentDate.getMonth(), currentDate.getDate());
      const l5yDate = new Date(currentDate.getFullYear() - 5, 0, 1);
      let index = -1;

      let divSums = { ytd: 0, ttm: 0, l5y: 0, all: 0 };

      for (const t of this.transactions) {
        if (!this.projectedDividends && t.date > currentDate) {
          continue;
        }
        if (t.type == "DIV" && (index = instrumentDivValues.findIndex(value => value.code == t.instrumentCode)) > -1) {
          const iDivValues = instrumentDivValues[index];
          const value = t.units * t.amountPerUnit - t.fees - t.taxes;
          let valueInBaseCurrency = 0;

          const finder = (item, index, arr) => {
            return t.date >= item && ((index + 1 < arr.length && t.date < arr[index + 1]) || index + 1 == arr.length);
          };

          const yearlyIndex = dividendTimelineData.yearly.dates.findIndex(finder);
          const halfYearlyIndex = dividendTimelineData.halfYearly.dates.findIndex(finder);
          const quarterlyIndex = dividendTimelineData.quarterly.dates.findIndex(finder);
          const monthlyIndex = dividendTimelineData.monthly.dates.findIndex(finder);

          //dividendTimelineData.halfYearly.

          if (yearlyIndex < 0 || halfYearlyIndex < 0 || quarterlyIndex < 0 || monthlyIndex < 0) {
            console.log(`Negative Index @  ${yearlyIndex} || ${halfYearlyIndex} || ${quarterlyIndex} || ${monthlyIndex}`);
          }

          switch (t.currencyCode) {
            case "USD":
              valueInBaseCurrency = value * this.fx.usd;
              //
              dividendTimelineData.yearly.usd[yearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.halfYearly.usd[halfYearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.quarterly.usd[quarterlyIndex] += valueInBaseCurrency;
              dividendTimelineData.monthly.usd[monthlyIndex] += valueInBaseCurrency;
              break;
            case "GBX":
              valueInBaseCurrency = value * this.fx.gbx;
              //
              dividendTimelineData.yearly.gbp[yearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.halfYearly.gbp[halfYearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.quarterly.gbp[quarterlyIndex] += valueInBaseCurrency;
              dividendTimelineData.monthly.gbp[monthlyIndex] += valueInBaseCurrency;
              break;
            case "EUR":
              valueInBaseCurrency = value * this.fx.eur;
              //
              dividendTimelineData.yearly.eur[yearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.halfYearly.eur[halfYearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.quarterly.eur[quarterlyIndex] += valueInBaseCurrency;
              dividendTimelineData.monthly.eur[monthlyIndex] += valueInBaseCurrency;
              break;
            case "USX":
              valueInBaseCurrency = value * this.fx.usx;
              //
              dividendTimelineData.yearly.usd[yearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.halfYearly.usd[halfYearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.quarterly.usd[quarterlyIndex] += valueInBaseCurrency;
              dividendTimelineData.monthly.usd[monthlyIndex] += valueInBaseCurrency;
              break;
            case "GBP":
              valueInBaseCurrency = value * this.fx.gbp;
              //
              dividendTimelineData.yearly.gbp[yearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.halfYearly.gbp[halfYearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.quarterly.gbp[quarterlyIndex] += valueInBaseCurrency;
              dividendTimelineData.monthly.gbp[monthlyIndex] += valueInBaseCurrency;
              break;
            case "EUX":
              valueInBaseCurrency = value * this.fx.eux;
              //
              dividendTimelineData.yearly.eur[yearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.halfYearly.eur[halfYearlyIndex] += valueInBaseCurrency;
              dividendTimelineData.quarterly.eur[quarterlyIndex] += valueInBaseCurrency;
              dividendTimelineData.monthly.eur[monthlyIndex] += valueInBaseCurrency;
              break;
          }

          //
          dividendTimelineData.yearly.total[yearlyIndex] += valueInBaseCurrency;
          dividendTimelineData.halfYearly.total[halfYearlyIndex] += valueInBaseCurrency;
          dividendTimelineData.quarterly.total[quarterlyIndex] += valueInBaseCurrency;
          dividendTimelineData.monthly.total[monthlyIndex] += valueInBaseCurrency;

          divSums.all += valueInBaseCurrency;
          iDivValues.all += valueInBaseCurrency;

          if (t.date >= l5yDate) {
            iDivValues.l5y += valueInBaseCurrency;
            divSums.l5y += valueInBaseCurrency;
            if (t.date >= ttmDate) {
              iDivValues.ttm += valueInBaseCurrency;
              divSums.ttm += valueInBaseCurrency;
              if (t.date >= ytdDate) {
                iDivValues.ytd += valueInBaseCurrency;
                divSums.ytd += valueInBaseCurrency;
              }
            }
          }
        }
      }
      divSums = { ytd: formatter.format(divSums.ytd), ttm: formatter.format(divSums.ttm), l5y: formatter.format(divSums.l5y), all: formatter.format(divSums.all) };

      return { sums: divSums, instrumentAccummulations: instrumentDivValues, dividendTimelineData: dividendTimelineData };
    }
  },
  data() {
    return {
      accumulatedDividendsRange: 3, //0- ytd, 1- TTM, 2 - l5y, 3 - all
      timeIncrements: 3, // 0 - monthly, 1 - quaterly, 2 - half-yearly, 3 - yearly
      lineChartData: {
        labels: ["GRMD", "RDSD", "PFDD", "GRCD"],
        datasets: [
          { label: "USD", data: [20, 52, 17, 4] },
          { label: "GBP", data: [4, 1, 7, 2] }
        ]
      },
      doughnutChartData: {
        datasets: [
          {
            data: [10, 20, 30]
          }
        ],

        // These labels appear in the legend and in the tooltips when hovering different arcs
        labels: ["Red", "Yellow", "Blue"]
      },
      localeIndex: 0,
      projectedDividends: true,

      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    };
  },
  methods: {
    ...mapActions(["getBasicDataAction", "ensureAuthorized"]),
    prepareTimelineData(timeline, months, end) {
      const d = new Date(2018, 0, 1);
      timeline.dates.push(new Date(2018, 0, 1));
      while (d.setMonth(d.getMonth() + months) < end) {
        //;
        timeline.dates.push(new Date(d.getFullYear(), d.getMonth(), 1));
      }
      const size = timeline.dates.length;
      timeline.usd = Array(size).fill(0);
      timeline.gbp = Array(size).fill(0);
      timeline.eur = Array(size).fill(0);
      timeline.total = Array(size).fill(0);
    },
    getTimeSeriesDataset(xSeries, ySeries) {
      const l = xSeries.length < ySeries.length ? xSeries.length : ySeries.length;
      const result = [];
      for (let i = 0; i < l; i++) {
        result.push({ t: xSeries[i], y: ySeries[i] });
      }
      return result;
    }
  },
  mounted() {
    if (!this.isBasicDataGotten) {
      const thisInstanceReference = this;
      //   //will updated authenticated state
      this.ensureAuthorized().then(function() {
        if (thisInstanceReference.authenticated == true) {
          thisInstanceReference.getBasicDataAction();
        } else {
          thisInstanceReference.$router.push("/");
        }
      });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.chart-container {
  position: relative;
  margin: auto;
  width: 80vw;
}
</style>