<!--  
***  2020.06.05 Now uses centralized functions for enforcing/restoring authourization
***  2020.06.13 Implemented display of dividend per instrument
***  2020.06.17 Implemented display dividend timeline chart
***  2020.07.01 Implemented instrument/portfolio/currency valuation barcharts
***  2020.07.04 Fixed tooltips format for barcharts and doughnut charts. Introduced custom Chart.js colours. Added quaterly dividend growth chart
***             Instrument Holdings valuation chart tooltip now includes the number of units as well
***  2020.07.05 Implemented dashboard settings to control the plot parameters (range, time frequency, currency, etc.)
***  2020.09.23 Updated to use common revised values and fxr server API data model
***  2021.01.14 Update - Provisional data is excluded based on the provisional property of the transaction, no longer based on date being later than current
***  2021.02.03 Client-side support added for inactive instruments (without quote data)
***  2021.02.17 Instrument dividend chart can now combine divs of sold out positions, depending on user settings
***  2021.09.02 Enforced "Instrument Holdings" chart has zero min on y axis. USD now default currency.
-->

<template>
  <div id="layoutSidenav_content">
    <div>
      <main>
        <div class="container-fluid">
          <div class="row mt-3">
            <div class="col-lg-11">
              <h3 class="text-primary float-left">Dashboard</h3>
            </div>
            <div class="col-lg-1">
              <b-button-group class="ml-3 float-right">
                <b-button variant="primary" v-b-modal.dashboard-modal>
                  <font-awesome-icon :icon="['fas', 'cog']" />
                </b-button>
              </b-button-group>
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
                  <h1>{{valuations.portfoliosTotalValue}}</h1>
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
          <div class="chart-container mt-4 mb-4">
            <bar-chart :chartData="instrumentHoldingsChartInfo.chartData" :options="instrumentHoldingsChartInfo.chartOptions"></bar-chart>
          </div>
          <hr />
          <!-- -->
          <div class="row">
            <div class="col-xl-4 col-md-6">
              <doughnut :chartData="portfolioHoldingsChartInfo.chartData" :options="portfolioHoldingsChartInfo.chartOptions"></doughnut>
            </div>
            <div class="col-xl-4 col-md-6">
              <doughnut :chartData="currencyHoldingsChartInfo.chartData" :options="currencyHoldingsChartInfo.chartOptions"></doughnut>
            </div>
            <div class="col-xl-4 col-md-6">
              <doughnut :chartData="currencyHoldingsChartInfo.chartData" :options="currencyHoldingsChartInfo.chartOptions"></doughnut>
            </div>
          </div>
          <hr />
          <!-- -->
          <div class="chart-container mt-4 mb-4">
            <bar-chart :chartData="instrumentDividendsChartInfo.chartData" :options="instrumentDividendsChartInfo.chartOptions"></bar-chart>
          </div>
          <hr />
          <!-- -->
          <div class="chart-container mt-4 mb-4">
            <bar-chart :chartData="dividendGrowthChartInfo.chartData" :options="dividendGrowthChartInfo.chartOptions"></bar-chart>
          </div>
          <hr />
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

    <!-- DASHBOARD Settings MODAL  -->
    <div>
      <b-modal id="dashboard-modal" @ok="updateDashboardSettings()" @hidden="cancelDialog()" centered v-bind:title="'Dashboard Settings'">
        <form>
          <div class="row">
            <div class="form-group col-md-12 text-left">
              <label for="optBaseCurrency">Base Currency:</label>
              <b-form-select id="optBaseCurrency" v-model="dialogSettings.currency">
                <!--<b-form-select-option :value="null" disabled>Select an option</b-form-select-option> -->
                <b-form-select-option v-for="(c, index) in currencyOptions" v-bind:key="index" v-bind:value="index">{{c}}</b-form-select-option>
              </b-form-select>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-md-6 text-left">
              <label for="optCumDividends">Cumulative Dividends:</label>
              <b-form-select id="optCumDividends" v-model="dialogSettings.cumDiv">
                <!--<b-form-select-option :value="null" disabled>Select an option</b-form-select-option> -->
                <b-form-select-option v-for="(cd, index) in cumDivOptions" v-bind:key="index" v-bind:value="index">{{cd}}</b-form-select-option>
              </b-form-select>
            </div>
            <div class="form-group col-md-6 text-left">
              <label for="optDivFrequency">Dividend Frequency:</label>
              <b-form-select id="optDivFrequency" v-model="dialogSettings.divFreq">
                <!--<b-form-select-option :value="null" disabled>Select an option</b-form-select-option> -->
                <b-form-select-option v-for="(df, index) in divFreqOptions" v-bind:key="index" v-bind:value="index">{{df}}</b-form-select-option>
              </b-form-select>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-md-12 text-left">
              <b-form-checkbox id="optIncludeProjected" v-model="dialogSettings.projectedDividends">Include provisional data</b-form-checkbox>
            </div>
          </div>
          <div class="row">
            <div class="form-group col-md-12 text-left">
              <b-form-checkbox id="optAggregateInactiveDividends" v-model="dialogSettings.aggregateInactiveDivs">Aggregate dividends of inactive instruments</b-form-checkbox>
            </div>
          </div>
        </form>
      </b-modal>
    </div>
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
    ...mapState(["isBasicDataGotten", "authenticated", "transactions", "supportedInstruments", "fxr", "values", "displayFormats", "currencies", "portfolios"]),
    fx() {
      const baseCurrencyId = this.displayFormats[this.settings.currency].baseCurrencyId;
      const fx = {
        usd: this.fxr.find(item => item.from == 1001101 && item.to == baseCurrencyId).value,
        gbp: this.fxr.find(item => item.from == 1001103 && item.to == baseCurrencyId).value,
        eur: this.fxr.find(item => item.from == 1001105 && item.to == baseCurrencyId).value,
        usx: this.fxr.find(item => item.from == 1001102 && item.to == baseCurrencyId).value,
        gbx: this.fxr.find(item => item.from == 1001104 && item.to == baseCurrencyId).value,
        eux: this.fxr.find(item => item.from == 1001106 && item.to == baseCurrencyId).value
      };
      return fx;
    },    
     dividends() {
      const instrumentDivValues = [];
      const format = this.displayFormats[this.settings.currency];
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

      //https://stackoverflow.com/a/35970005  this.supportedInstruments.foreach(instrument => {}); will not work
      for (const instrument of this.supportedInstruments) {
        instrumentDivValues.push({
          ytd: 0,
          ttm: 0,
          l5y: 0,
          all: 0,
          code: instrument.code,
          active: instrument.active
        });
      }

      // extra slot for sold instruments
      instrumentDivValues.push({
          ytd: 0,
          ttm: 0,
          l5y: 0,
          all: 0,
          code: 'SOLD',
          active: false
        });

      const ytdDate = new Date(currentDate.getFullYear(), 0, 1);
      const ttmDate = new Date(currentDate.getFullYear() - 1, currentDate.getMonth(), currentDate.getDate());
      const l5yDate = new Date(currentDate.getFullYear() - 5, 0, 1);
      let index = -1;

      const soldInstrumentDivValuesIndex = instrumentDivValues.length -1;

      let divSums = { ytd: 0, ttm: 0, l5y: 0, all: 0 };

      for (const t of this.transactions) {
        if (this.settings.projectedDividends == false && t.provisional == true /*t.date > currentDate*/) {
          continue;
        }
        if (t.type == "DIV" && (index = instrumentDivValues.findIndex(value => value.code == t.instrumentCode)) > -1) {

          const iDivValues = instrumentDivValues[index].active == false && this.settings.aggregateInactiveDivs ?  instrumentDivValues[soldInstrumentDivValuesIndex]: instrumentDivValues[index];
          const value = t.units * t.amountPerUnit - t.fees - t.taxes;
          let valueInBaseCurrency = 0;

          const finder = (item, index, arr) => {
            return t.date >= item && ((index + 1 < arr.length && t.date < arr[index + 1]) || index + 1 == arr.length);
          };

          const yearlyIndex = dividendTimelineData.yearly.dates.findIndex(finder);
          const halfYearlyIndex = dividendTimelineData.halfYearly.dates.findIndex(finder);
          const quarterlyIndex = dividendTimelineData.quarterly.dates.findIndex(finder);
          const monthlyIndex = dividendTimelineData.monthly.dates.findIndex(finder);

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
     },
    valuations() {
      const vInstruments = []; //labels: [], unitValue: [], holdings: [] };
      const vPortfolios = []; //{ labels: [], holdings: [] };
      const vCurrencies = []; //{ labels: [], holdings: [] };

      for (const instrument of this.supportedInstruments) {
        const valueIndex = this.values.findIndex(value => value.code == instrument.id);
        if (valueIndex > -1) {
          vInstruments.push({ label: instrument.code, valuation: 0, units: 0, unitValue: this.values[valueIndex].value });
        }else{
          vInstruments.push({ label: instrument.code, valuation: 0, units: 0, unitValue: 0 }); // this is an inactive instrument - we dont care about it!
        }
      }

      for (const p of this.portfolios) {
        vPortfolios.push({ label: p.code, valuation: 0 });
      }

      for (const c of this.currencies) {
        vCurrencies.push({ label: c.code, valuation: 0 });
      }

      let stockIndex = -1,
        portfolioIndex = -1,
        currencyIndex = -1;

      for (const t of this.transactions) {
        if (t.type == "BUY" || t.type == "SELL") {
          stockIndex = vInstruments.findIndex(value => value.label == t.instrumentCode);
          portfolioIndex = vPortfolios.findIndex(value => value.label == t.portfolioCode);
          currencyIndex = vCurrencies.findIndex(value => value.label == t.currencyCode);
          //console.log(`Will check ${t.id}>> STOCK:${stockIndex}, PORT:${portfolioIndex}, CURRENCY:${currencyIndex}`);
          if (stockIndex > -1 && portfolioIndex > -1 && currencyIndex > -1) {
            const units = t.type == "BUY" ? t.units : -t.units;
            const value = units * vInstruments[stockIndex].unitValue;
            let valueInBaseCurrency = 0;
            //console.log(`DATA:: Units ${units}>> Rate:${vInstruments[stockIndex].unitValue}, Value:${value}, CURRENCY:${currencyIndex}`);
            switch (t.currencyCode) {
              case "USD":
                valueInBaseCurrency = value * this.fx.usd;
                break;
              case "GBX":
                valueInBaseCurrency = value * this.fx.gbx;
                break;
              case "EUR":
                valueInBaseCurrency = value * this.fx.eur;
                break;
              case "USX":
                valueInBaseCurrency = value * this.fx.usx;
                break;
              case "GBP":
                valueInBaseCurrency = value * this.fx.gbp;
                break;
              case "EUX":
                valueInBaseCurrency = value * this.fx.eux;
                break;
            }
            vInstruments[stockIndex].units += units;
            vInstruments[stockIndex].valuation += valueInBaseCurrency;
            vPortfolios[portfolioIndex].valuation += valueInBaseCurrency;
            vCurrencies[currencyIndex].valuation += valueInBaseCurrency;
          }
        }
      }

      // compact and sort instrument valuations + remove those with zero value
      const instrumentsValuations = { labels: [], valuations: [], units: [] };
      this.getCompactValuation(vInstruments, instrumentsValuations);

      // compact and sort currency valuations + remove those with zero value
      const currencyValuations = { labels: [], valuations: [] };
      this.getCompactValuation(vCurrencies, currencyValuations);

      // compact and sort portfolio valuations + remove those with zero value
      const portfolioValuations = { labels: [], valuations: [] };
      let portfoliosTotal = this.getCompactValuation(vPortfolios, portfolioValuations);

      const format = this.displayFormats[this.settings.currency];
      const formatter = new Intl.NumberFormat(format.locale, { style: "currency", currency: format.currency });
      portfoliosTotal = formatter.format(portfoliosTotal);
      //

      return { instruments: instrumentsValuations /*, instruments: vInstruments*/, portfolios: portfolioValuations, currencies: currencyValuations /*vCurrencies */, portfoliosTotalValue: portfoliosTotal };
    },
    instrumentDividendsChartInfo() {
      const labels = [];
      const data = [];
      let seriesLabel = null;

      if (this.dividends.instrumentAccummulations.length > 0) {
        const safeinstrumentAccummulations = this.dividends.instrumentAccummulations.slice();

        switch (this.settings.cumDiv) {
          case 0:
            seriesLabel = "Dividend (YTD)";
            safeinstrumentAccummulations.sort(function(a, b) {
              return b.ytd - a.ytd;
            });
            for (const iDivValues of safeinstrumentAccummulations) {
              if (iDivValues.ytd > 0) {
                labels.push(iDivValues.code);
                data.push(iDivValues.ytd);
              }
            }
            break;
          case 1:
            seriesLabel = "Dividend (TTM)";
            safeinstrumentAccummulations.sort(function(a, b) {
              return b.ttm - a.ttm;
            });
            for (const iDivValues of safeinstrumentAccummulations) {
              if (iDivValues.ttm > 0) {
                labels.push(iDivValues.code);
                data.push(iDivValues.ttm);
              }
            }
            break;
          case 2:
            seriesLabel = "Dividend (5YRS)";
            safeinstrumentAccummulations.sort(function(a, b) {
              return b.l5y - a.l5y;
            });
            for (const iDivValues of safeinstrumentAccummulations) {
              if (iDivValues.l5y > 0) {
                labels.push(iDivValues.code);
                data.push(iDivValues.l5y);
              }
            }
            break;
          default:
            seriesLabel = "Dividend (ALL)";
            safeinstrumentAccummulations.sort(function(a, b) {
              return b.all - a.all;
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
        },
        tooltips: {
          //https://stackoverflow.com/questions/25880767/chart-js-number-format
          callbacks: {
            label: function(tooltipItem) {
              return tooltipItem.yLabel.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, "$&,");
            }
          }
        }
      };

      const result = {
        chartData: {
          labels: labels,
          datasets: [
            {
              label: seriesLabel,
              data: data,
              backgroundColor: this.chartColors.bsb,
              //hoverBackgroundColor: "rgba(52,58,64, 0.5)"
              hoverBackgroundColor: this.chartColors.grey
            }
          ]
        },
        chartOptions: options
      };

      return result;
    },
    dividendTimelineChartInfo() {
      const datasets = [];
      if (this.dividends.dividendTimelineData.quarterly.dates.length > 0) {
        //let safeinstrumentAccummulations = this.dividends.instrumentAccummulations.slice();
        let dataSource = null;
        let shift = 0;
        switch (this.settings.divFreq) {
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
        datasets.push({ backgroundColor: "rgba(0,123,255, 1.0)", label: "USD", data: this.getTimeSeriesDataset(midPeriodDates, dataSource.usd) });
        datasets.push({ fill: "-1", backgroundColor: "rgba(220, 0, 0, 0.2)", label: "GBP", data: this.getTimeSeriesDataset(midPeriodDates, dataSource.gbp) });

        // //round about way of stacking - chart.js
        // const eurusd = dataSource.eur.map((e, i) => e + dataSource.usd[i]);
        // const eurusdgbp = dataSource.eur.map((e, i) => e + dataSource.usd[i] + dataSource.gbp[i]);
        // datasets.push({ backgroundColor: "rgba(0,123,255, 1.0)", label: "USD", data: this.getTimeSeriesDataset(midPeriodDates, eurusd) });
        // datasets.push({ fill: "-1", backgroundColor: "rgba(220, 0, 0, 0.2)", label: "GBP", data: this.getTimeSeriesDataset(midPeriodDates, eurusdgbp) });
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
    dividendGrowthChartInfo() {
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
        },
        tooltips: {
          //https://stackoverflow.com/questions/25880767/chart-js-number-format
          callbacks: {
            label: function(tooltipItem) {
              return tooltipItem.yLabel.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, "$&,");
              //return Number.parseFloat(tooltipItem.value).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, "$&,");
            }
          }
        }
      };

      let chartData = null;

      const datasets = [];
      if (this.dividends.dividendTimelineData.quarterly.dates.length > 0) {
        const ds = this.dividends.dividendTimelineData.quarterly;
        const NUMBER_OF_YEARS = 5;
        const d = new Date();
        const currentYear = d.getFullYear();
        const earliestAllowedQuater = new Date(currentYear - NUMBER_OF_YEARS + 1, 0, 1);
        let startIndex = 0;
        const yearLabels = [];
        for (; startIndex < ds.dates.length; startIndex++) {
          if (ds.dates[startIndex] >= earliestAllowedQuater) {
            for (let j = ds.dates[startIndex].getFullYear(); j <= currentYear; j++) {
              yearLabels.push(j + "");
            }
            break;
          }
        }
        this.getGrowthSeriesDataset(datasets, { label: "Q1", backgroundColor: this.chartColors.red, data: [] }, ds, startIndex);
        this.getGrowthSeriesDataset(datasets, { label: "Q2", backgroundColor: this.chartColors.orange, data: [] }, ds, startIndex + 1);
        this.getGrowthSeriesDataset(datasets, { label: "Q3", backgroundColor: this.chartColors.purple, data: [] }, ds, startIndex + 2);
        this.getGrowthSeriesDataset(datasets, { label: "Q4", backgroundColor: this.chartColors.green, data: [] }, ds, startIndex + 3);
        chartData = { labels: yearLabels, datasets: datasets };
      }

      return { chartData: chartData, chartOptions: options };
    },
    instrumentHoldingsChartInfo() {
      const seriesLabel = "Instruments Holdings";
      const thisInstance = this;
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
              ticks: { fontSize: 16, fontStyle: "bold", fontColor: "#007bff", suggestedMin: 0 }
            }
          ]
        },
        tooltips: {
          //https://stackoverflow.com/questions/25880767/chart-js-number-format
          callbacks: {
            label: function(tooltipItem) {
              const index = thisInstance.valuations.instruments.labels.findIndex(value => value == tooltipItem.xLabel);
              const units = thisInstance.valuations.instruments.units[index];
              return `${tooltipItem.yLabel.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, "$&,")} (${units})`;
              //return Number.parseFloat(tooltipItem.value).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, "$&,");
            }
          }
        }
      };
      const result = {
        chartData: {
          labels: this.valuations.instruments.labels,
          datasets: [
            {
              label: seriesLabel,
              data: this.valuations.instruments.valuations,
              backgroundColor: this.chartColors.bsb,
              hoverBackgroundColor: this.chartColors.grey
            }
          ]
        },
        chartOptions: options
      };
      return result;
    },
    portfolioHoldingsChartInfo() {
      const options = {
        responsive: true,
        maintainAspectRatio: false,
        tooltips: {
          //https://stackoverflow.com/questions/25880767/chart-js-number-format,   https://www.chartjs.org/docs/latest/configuration/tooltip.html#tooltip-item-interface
          callbacks: {
            label: function(tooltipItem, data) {
              //return data.labels[tooltipItem.index]; This will be the dataset label
              //return  data.datasets[0].data[tooltipItem.index] ; this will be the value
              return data.labels[tooltipItem.index] + ": " + data.datasets[0].data[tooltipItem.index].toFixed(2).replace(/\d(?=(\d{3})+\.)/g, "$&,"); //  tooltipItem.label; //.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, "$&,");
            }
          }
        }
      };
      const result = {
        chartData: {
          labels: this.valuations.portfolios.labels,
          datasets: [
            {
              data: this.valuations.portfolios.valuations,
              //backgroundColor: "rgba(0,123,255, 1.0)",
              //hoverBackgroundColor: "rgba(52,58,64, 0.5)"
              backgroundColor: [this.chartColors.red, this.chartColors.orange, this.chartColors.yellow, this.chartColors.green, this.chartColors.blue, this.chartColors.purple]
            }
          ]
        },
        chartOptions: options
      };
      return result;
    },
    currencyHoldingsChartInfo() {
      const options = {
        responsive: true,
        maintainAspectRatio: false,
        tooltips: {
          //https://stackoverflow.com/questions/25880767/chart-js-number-format,   https://www.chartjs.org/docs/latest/configuration/tooltip.html#tooltip-item-interface
          callbacks: {
            label: function(tooltipItem, data) {
              //return data.labels[tooltipItem.index]; This will be the dataset label
              //return  data.datasets[0].data[tooltipItem.index] ; this will be the value
              return data.labels[tooltipItem.index] + ": " + data.datasets[0].data[tooltipItem.index].toFixed(2).replace(/\d(?=(\d{3})+\.)/g, "$&,"); //  tooltipItem.label; //.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, "$&,");
            }
          }
        }
      };
      const result = {
        chartData: {
          labels: this.valuations.currencies.labels,
          datasets: [
            {
              data: this.valuations.currencies.valuations,
              //backgroundColor: "rgba(0,123,255, 1.0)",
              //hoverBackgroundColor: this.chartColors.grey,
              backgroundColor: [this.chartColors.red, this.chartColors.orange, this.chartColors.yellow, this.chartColors.green, this.chartColors.blue, this.chartColors.purple]
            }
          ]
        },
        chartOptions: options
      };
      return result;
    }
  },
  data() {
    return {
      cumDivOptions: ["Year to date", "Trailing 12 months", "Last 5 years", "Entire Range"],
      divFreqOptions: ["Monthly", "Quarterly", "Half-yearly", "Yearly"],
      currencyOptions: ["British Pounds", "US Dollars"],
      settings: { cumDiv: 3, divFreq: 2, currency: 1, projectedDividends: true, aggregateInactiveDivs: true },
      dialogSettings: { cumDiv: 3, divFreq: 2, currency: 1, projectedDividends: true, aggregateInactiveDivs: true },
      chartColors: { bsb: "rgba(0,123,255, 1.0)", red: "rgb(255, 99, 132)", orange: "rgb(255, 159, 64)", yellow: "rgb(255, 205, 86)", green: "rgb(75, 192, 192)", blue: "rgb(54, 162, 235)", purple: "rgb(153, 102, 255)", grey: "rgb(231,233,237)" }
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
    },
    getGrowthSeriesDataset(datasets, dataset, datasource, offset) {
      let i = offset;
      for (; i < datasource.dates.length; ) {
        dataset.data.push(datasource.total[i]);
        i += 4;
      }
      datasets.push(dataset);
    },
    getCompactValuation(dataset, results) {
      //make a safe copy of and sort from high to low
      const safeValuations = dataset.slice();
      safeValuations.sort(function(a, b) {
        return b.valuation - a.valuation;
      });
      // compact valuations - remove those with zero value
      //const vs = { labels: [], valuations: [] };
      let idx = 0;
      let total = 0;
      for (; idx < safeValuations.length; idx++) {
        if (safeValuations[idx].valuation > 0) {
          results.labels.push(safeValuations[idx].label);
          results.valuations.push(safeValuations[idx].valuation);
          if (results.units != null) {
            results.units.push(safeValuations[idx].units);
          }
          total += safeValuations[idx].valuation;
        } else {
          break;
        }
      }
      return total;
    },
    updateDashboardSettings() {
      this.settings = { cumDiv: this.dialogSettings.cumDiv, divFreq: this.dialogSettings.divFreq, currency: this.dialogSettings.currency, projectedDividends: this.dialogSettings.projectedDividends , aggregateInactiveDivs: this.dialogSettings.aggregateInactiveDivs};
    },
    cancelDialog() {
      this.dialogSettings = { cumDiv: this.settings.cumDiv, divFreq: this.settings.divFreq, currency: this.settings.currency, projectedDividends: this.settings.projectedDividends, aggregateInactiveDivs: this.settings.aggregateInactiveDivs };
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
.charts-container {
  position: relative;
  margin: auto;
  width: 40w;
}
</style>