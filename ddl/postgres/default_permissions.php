<?php

$permission_tr = [
	'full' => [
		'r' => 'arwdDxt', // tables, views
		'S' => 'rwU',     // sequences
		'f' => 'X',       // functions
	],
	'write' => [
		'r' => 'arwd',
		'S' => 'U',
		'f' => 'X',
	],
	'read' => [
		'r' => 'r',
		'S' => '',
		'f' => 'X',
	],
];

$permissions = [
	'tm_cz' => [
		'tm_cz' => 'full',
	],
	'tm_lz' => [
		'tm_cz' => 'full',
		'tm_lz' => 'full',
	],
	'tm_wz' => [
		'tm_cz' => 'full',
		'tm_wz' => 'full',
	],
	'i2b2metadata' => [
		'tm_cz' => 'full',
		'biomart_user' => 'read',
	],
	'i2b2demodata' => [
		'tm_cz' => 'full',
		'biomart_user' => 'read',
	],
	'biomart' => [
		'tm_cz' => 'full',
		'biomart_user' => 'read',
	],
	'deapp' => [
		'tm_cz' => 'full',
		'biomart_user' => 'read',
	],
	'searchapp' => [
		'tm_cz' => 'full',
		'biomart_user' => 'write',
	],
];

$stdout = fopen('php://stdout', 'w');
foreach ($permissions as $schema => $spec) {
	foreach ($spec as $user => $perm) {
		foreach (['r', 'S', 'f'] as $type) {
			fputcsv($stdout, array($schema, $user, $type, $permission_tr[$perm][$type]), "\t");
		}
	}
}
