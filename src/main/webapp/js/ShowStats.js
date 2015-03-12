/**
  * Create a bar chart representing the current rating situation.
  */

$('a[data-toggle="tab"][href="#stats"]').on('shown.bs.tab', function (e) {
    var chartAlreadyCreated = (e.target.chartCreated === true);

    if (!chartAlreadyCreated) {
        // Extract current stats from table
        var choices = [];
        var ratings = [];

        $('[wicket\\:id="choicetablerow"]').each(function (idx, elem) {
            var choice = $(elem).find('[wicket\\:id="choice"]').text();
            var rating = $(elem).find('[wicket\\:id="avgrating"]').text();

            choices.push(choice);
            ratings.push(rating);
        });

        // Draw chart
        var data = {
            labels: choices,
            datasets: [
                {
                    data: ratings,
                    fillColor: "rgba(160, 160, 160, 0.5)",
                    strokeColor: "rgba(160, 160, 160, 0.8)",
                    highlightFill: "rgba(49, 121, 181, 0.75)",
                    highlightStroke: "rgba(49, 121, 181, 1)",
                }
            ]
        };
        var options = {
            responsive: true,
            maintainAspectRatio: false
        };

        var canvas = document.getElementById('choicechart');
        var ctx = canvas.getContext("2d");
        var statsChart = new Chart(ctx).Bar(data, options);

        // Remember the chart and the fact that we've already created it
        e.target.chartCreated = true;
        e.target.chart = statsChart;
    }
});