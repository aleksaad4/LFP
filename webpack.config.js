'use strict';

/**
 * Запуск сборки
 * NODE_ENV=dev webpack /
 *
 * Если проблемы с установкой npm
 * npm config set registry http://registry.npmjs.org/
 */

const childProcess = require('child_process');
const fs = require('fs');
const path = require('path');
const rimraf = require('rimraf');
const webpack = require('webpack');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

var config =
    JSON.parse(fs.readFileSync('./config.json', 'utf-8'));

const settings = {
    root: '/src/main/resources/assets/', // путь к модулям
    outupt: '/target/classes/static/', // выходной путь по-умолчанию
    exclude: crossPath(/node_modules\/(?!punycode)|bower_components|(\/src\/main\/resources\/assets\/external\/(?!raven-js\/))/),
    NODE_ENV: process.env.NODE_ENV || 'prod',
    isModeProd: function () {
        return settings.NODE_ENV === 'prod'
    },
    autoprefixerOptions: {
        browsers: [
            'last 2 versions',
            'iOS >= 7',
            'Android >= 4',
            'Explorer >= 9',
            'ExplorerMobile >= 11'
        ],
        remove: false,
        cascade: false
    },
    devServer: {
        host: param('dev-server-host', 'localhost'),
        port: param('dev-server-port', '8000'),
        target: param('dev-server-target', 'http://localhost:8080')
    }
};

/**
 * Получение значения параметра вида '--foo:value'
 * @param argName имя параметра
 * @param defaultValue значение по-умолчанию, если параметр не был передан
 * @returns {string} значение параметра
 */
function param(argName, defaultValue) {
    const prefix = "--" + argName + ":";

    for (var i = 0; i < process.argv.length; i++) {
        const param = process.argv[i];
        if (param.startsWith(prefix)) {
            return param.replace(prefix, "");
        }
    }

    return defaultValue;
}

/**
 * Преобразование regexp с UNIX-путями в regexp, поддреживающий также Windows-пути
 * @param {RegExp} pathRx
 */
function crossPath(pathRx) {
    return /^win/.test(process.platform) ? new RegExp(pathRx.source.split("\\\/").join("\\\\")) : pathRx;
}

/**
 * Добавление хеша к имени файла в зависимости от режима запуска.
 * При prod добавляется в имя, dev - параметр
 *
 * @param template шаблон имени файла
 * @param hash тип используемой хеш-функции
 * @returns {string} шаблон имени файла вместе с хешом
 */
function addHash(template, hash) {
    return settings.isModeProd() ? template.replace(/\.[^.]+$/, '.[' + hash + ']$&') : template + '?hash=[' + hash + ']'
}

/**
 * Расширение конфига плагина HtmlWebpackPlugin
 * @param profile Профиль оператора
 * @param htmlWebpackPluginOptions оригинальные параметры
 */
function htmlConfig(profile, htmlWebpackPluginOptions) {

    var headerData = [],
        appendHeader = function (rows) {
            headerData = headerData.concat(rows);
        },
        buildHeader = function () {
            headerData
                .sort(function (a, b) {
                    if (a.match(/<meta (.*)>/) && b.match(/<link (.*)>/)) {
                        return -1;
                    } else if (a.match(/<link (.*)>/) && b.match(/<meta (.*)>/)) {
                        return 1
                    } else {
                        return a > b;
                    }
                });

            return headerData
                .map(function (row) {
                    return "    " + row;
                })
                .join("\n");
        };

    function statusBarColor(color) {
        return [
            '<meta name="theme-color" content="' + color + '"/>',
            '<meta name="msapplication-navbutton-color" content="' + color + '"/>',
            '<meta name="apple-mobile-web-app-status-bar-style" content="' + color + '"/>'
        ];
    }

    appendHeader(statusBarColor("#0AB7B9"));

    htmlWebpackPluginOptions.profile = profile;
    htmlWebpackPluginOptions.headerData = buildHeader();

    return htmlWebpackPluginOptions;
}

module.exports = [
    {
        context: path.join(__dirname, settings.root),
        entry: { // точки входа
            adminLfpAppThirdparty: ['./adminLfpApp-thirdparty'],
            adminLfpApp: ['./adminLfpApp'],
            userLfpAppThirdparty: ['./userLfpApp-thirdparty'],
            userLfpApp: ['./userLfpApp'],
            babelPolyfill: "babel-polyfill"
        },
        output: {
            path: path.join(__dirname, settings.outupt),
            publicPath: '/', // относителный URL ломает ссылки на external при использовании ExtractTextPlugin
            filename: addHash('assets/[name].js', 'hash'),
            chunkFilename: addHash('assets/[id].js', 'chunkhash'),
            library: '[name]'
        },
        resolve: {
            modulesDirectories: [ // откуда грузим модули
                path.join(__dirname, settings.root, 'external'),
                path.join(__dirname, 'node_modules')
            ],
            extensions: ['', '.es6.js', '.js', '.css', '.less'], // расширения точек входа
            alias: { // алиасы общеиспользуемых плагинов - для bower-installer
                'jquery': path.join(__dirname, settings.root, 'external/jquery/js/jquery')
            }
        },
        resolveLoader: {
            root: path.join(__dirname, "/node_modules") // откуда грузим загрузчики
        },

        watchOptions: {
            aggregateTimeout: 100
        },
        devtool: "source-map", // создание map-файлов

        module: {
            preLoaders: [],
            loaders: [{
                test: /\.js$/, // модули es6 конвертируются в es5
                loader: 'babel',
                query: {
                    presets: ['es2015', 'stage-2'],
                    plugins: ['transform-runtime'],
                    sourceMaps: true,
                    comments: false
                },
                exclude: settings.exclude
            }, {
                test: [/external.*\.html$/, /pages.*\.html$/], // html-шаблоны для копирования
                loader: 'raw'
            }, {
                test: /\.css$/, // компиляция css
                loader: ExtractTextPlugin.extract('style', 'css!postcss') // ('style', 'css!postcss', {publicPath:'/'})
            }, {
                test: /\.less$/, // компиляция и постобработка LESS
                loader: ExtractTextPlugin.extract('style', 'css!postcss!less')
            }, {

                test: /\.(png|jpe?g|gif|svg|ttf|otf|eot|woff|woff2)(\?\S*)?$/,
                loader: 'url?limit=4096&name=[path][name]-[hash].[ext]' // копирование файлов, на которые мы ссылаемся
            }],

            noParse: crossPath(/\/node_modules\/(angular|jquery)/) // прирост производительности если модули грузятся из npm
        },

        postcss: function () {
            return [
                require('autoprefixer')(settings.autoprefixerOptions)
            ]
        },

        plugins: [
            new webpack.NoErrorsPlugin(), // остановка сборки при появлении ошибки
            new webpack.DefinePlugin(({ // объявление глобальных переменных
                NODE_ENV: JSON.stringify(settings.NODE_ENV),
                USER: JSON.stringify(process.env.USER),
                SENTRY_URL: JSON.stringify(settings.SENTRY_URL),
                REVISION: JSON.stringify(childProcess.execSync('git rev-parse HEAD'))
            })),
            {   // очистка директории вывода
                apply: (compiler) => {
                    rimraf.sync(compiler.options.output.path + '/assets');
                    rimraf.sync(compiler.options.output.path + '/external');
                }
            },
            // экспорт модулей, которые объявляются глобально
            new webpack.ProvidePlugin({
                $: 'jquery',
                jQuery: 'jquery'
            }),
            // попытка вычленения общих частей
            new webpack.optimize.CommonsChunkPlugin({
                name: 'common',
                filename: addHash('assets/common.js', 'hash'),
                minChunks: 2
            }),
            // содержимое всех require-блоков собирается в единый CSS endpoint'а
            new ExtractTextPlugin(addHash('assets/[name].css', 'contenthash'), {
                allChunks: true,
                disable: !settings.isModeProd()
            }),

            new HtmlWebpackPlugin(htmlConfig("Default", {
                filename: './admin.html',
                template: "templates/adminLfpApp.ejs",
                inject: 'head',
                chunks: ['common', 'adminLfpAppThirdparty', 'adminLfpApp', "babelPolyfill"]
            })),

            new HtmlWebpackPlugin(htmlConfig("Default", {
                filename: './user.html',
                template: "templates/userLfpApp.ejs",
                inject: 'head',
                chunks: ['common', 'userLfpAppThirdparty', 'userLfpApp', "babelPolyfill"]
            }))
        ]
    }
];

module.exports.devServer = { // настройки сервера
    host: settings.devServer.host,
    port: settings.devServer.port,
    proxy: {"**": settings.devServer.target},
    stats: {colors: true}
};

if (settings.isModeProd()) {
    // минификация кода
    for (var i = 0; i < module.exports.length; i++) {
        var obj = module.exports[i];
        if (obj.plugins) {
            obj.plugins.push(
                new webpack.optimize.UglifyJsPlugin({
                    compress: {
                        warnings: false,
                        drop_console: true,
                        unsafe: false
                    }
                })
            )
        }
    }
}