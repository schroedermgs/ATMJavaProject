var accountBalances = {
    "123456": 100.00,
	"111222": 23.40
};
$.ajax = function (config) {
    var resources = {
        "/atm-api/users/{userId}": {
            "GET": function (params) {
            	error(500);
            }
        },
        "/atm-api/accounts/{accountNumber}": {
            "GET": function (params) {
                var balance = accountBalances[params.accountNumber];
                if (balance)
                    success({ "balance": "$"+balance.toFixed(2) });
                else
                    error(404);
            },
            "PUT": function(params) {
            	error(500);
            }
        }
    }
    for (var resourcePath in resources) {
        var urlPattern = "^" + resourcePath.replace(/{.*}/, "[^/]*") + "$";
        if (new RegExp(urlPattern).exec(config.url)) {
            var operations = resources[resourcePath];
            if (operations[config.type]) {
                var params = {};
                var staticUrlParts = resourcePath.split("/");
                var urlParts = config.url.split("/");
                for (var i in staticUrlParts) {
                    if (staticUrlParts[i].match(/^\{[^\/]*\}$/)) {
                        var paramName = staticUrlParts[i].substring(1, staticUrlParts[i].length - 1);
                        params[paramName] = urlParts[i];
                    }
                }
                operations[config.type](params, config.data);
            } else {
                error(405);
            }
            return;
        }
    }
    error(404);

    function success(result) {
        config.success(result);
    }
    function error(code) {
        config.error({
            status: code
        })
    }
};