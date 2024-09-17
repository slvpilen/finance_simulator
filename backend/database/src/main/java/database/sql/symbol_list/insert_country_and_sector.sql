
-- Insert Nordea Stabil Aksjer Global list
INSERT INTO financial.symbol_list (name, created_at)
VALUES 
('Country ETFs on US stock exchange', '2024-06-01'),
('US sector ETFs', '2024-06-01')
ON CONFLICT (name) DO NOTHING;

INSERT INTO financial.symbol_list_relation (symbol_list_id, symbol_id)
VALUES
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWA')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWO')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWK')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWZ')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWC')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'ECH')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'GXC')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'GXG')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWQ')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWG')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWH')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'PIN')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'IDX')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EIRL')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EIS')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWI')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWM')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWW')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWN')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EPU')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EPOL')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWS')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EZA')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWY')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWP')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWD')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWL')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWT')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'THD')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'TUR')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWU')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EUSA')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'VNM')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'ENZL')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'NORW')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EPHE')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'QAT')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'UAE')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'GREK')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EWJ')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'KSA')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'ARGT')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EDEN')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'EFNL')),
((SELECT id FROM financial.symbol_list WHERE name = 'Country ETFs on US stock exchange'), (SELECT id FROM financial.symbol WHERE ticker = 'KWT')),

((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLRE')),
((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLP')),
((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLU')),
((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLB')),
((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLV')),
((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLY')),
((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLE')),
((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLI')),
((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLF')),
((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLK')),
((SELECT id FROM financial.symbol_list WHERE name = 'US sector ETFs'), (SELECT id FROM financial.symbol WHERE ticker = 'XLC'));


