<!--
 * 2020.06.13  - Created
 *
-->
<script>
import { Doughnut, mixins } from "vue-chartjs";
const { reactiveProp } = mixins;

export default {
  extends: Doughnut,
  mixins: [reactiveProp],
  props: ["chartdata", "options"],
  mounted() {
    this.renderChart(this.chartdata, this.options);
  }
};
</script>