'use strict';

var gulp = require('gulp'),
    less = require('gulp-less'),
    exec = require('child_process').exec,
    webpack = require('webpack-stream'),
    fs = require('fs'),
    rimraf = require('rimraf')

function addToGit(path, postAction) {
    var command = 'git add ' + path;
    console.log("mark to add to git: '" + command + "'");
    exec(command, function (err, stdout, stderr) {
        if (!!stdout) console.log(stdout);
        if (!!stderr) console.log(stderr);
        if (!!err) console.log(err);

        if (!!postAction) postAction();
    });
}

gulp.task('env-dev', function() {
    process.env.NODE_ENV = 'dev';
});

/**
 * Выкачивание bower-пакетов, инсталляция существенной части в external и добавление в git
 */
gulp.task('bower-installer', function() {
    exec('node_modules/bower/bin/bower update', function (err, stdout, stderr) {

        if (!!stdout) console.log(stdout);
        if (!!stderr) console.log(stderr);
        if (!!err) {
            console.log(err);
            process.exit(1);
        } else {
            exec('node_modules/bower-installer/bower-installer.js', function (err, stdout, stderr) {
                if (!!stdout) console.log(stdout);
                if (!!stderr) console.log(stderr);
                if (!!err) console.log(err);

                // если добавился новый модуль, добавим в git
                addToGit('./src/main/resources/assets/external/');
            });
        }
    });
});

const eslint = require('gulp-eslint');

gulp.task('lint', () => {
    // ESLint ignores files with "node_modules" paths.
    // So, it's best to have gulp ignore the directory as well.
    // Also, Be sure to return the stream from the task;
    // Otherwise, the task may end before the stream has finished.
    return gulp.src(['src/main/resources/assets/js/**', '!node_modules/**'])
    // eslint() attaches the lint output to the "eslint" property
    // of the file object so it can be used by other modules.
        .pipe(eslint())
        // eslint.format() outputs the lint results to the console.
        // Alternatively use eslint.formatEach() (see Docs).
        .pipe(eslint.format())
        // To have the process exit with an error code (1) on
        // lint error, return the stream and pipe to failAfterError last.
        // .pipe(eslint.failAfterError())
        .pipe(eslint.result(result => {
            // Called for each ESLint result.
            console.log(`ESLint result: ${result.filePath}`);
            console.log(`# Messages: ${result.messages.length}`);
            console.log(`# Warnings: ${result.warningCount}`);
            console.log(`# Errors: ${result.errorCount}`);
        }));
});


/**
 * Компиляция модулей webpack'ом в target
 */
gulp.task('webpack', function() {
    return gulp.src('src/entry.js')
        .pipe(webpack({config: require('./webpack.config.js')}))
        .pipe(gulp.dest('target/classes/static/'));
});


gulp.task('default', ['webpack']);
gulp.task('webpack-dev', ['env-dev', 'webpack']);
